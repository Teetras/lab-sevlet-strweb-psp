package by.iba.connection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);
    private static final String PROPERTY_PATH = "db"; // Путь к файлу свойств с настройками подключения к базе данных
    private static final int INITIAL_CAPACITY = 5; // Начальная емкость пула соединений
    private ArrayBlockingQueue<Connection> freeConnections = new ArrayBlockingQueue<>(INITIAL_CAPACITY); // Очередь свободных соединений
    private ArrayBlockingQueue<Connection> releaseConnections = new ArrayBlockingQueue<>(INITIAL_CAPACITY); // Очередь взятых соединений
    private static ReentrantLock lock = new ReentrantLock(); // Блокировка для обеспечения потокобезопасности
    private volatile static ConnectionPool connectionPool; // Волатильная переменная для обеспечения видимости изменений в разных потоках

    public static ConnectionPool getInstance() {
        try {
            lock.lock();
            if (connectionPool == null) {
                connectionPool = new ConnectionPool();
            }
        } catch (Exception e) {
            logger.error("Can not get Instance", e);
            throw new RuntimeException("Can not get Instance", e);
        } finally {
            lock.unlock();
        }
        return connectionPool;
    }

    private ConnectionPool() throws SQLException {
        try {
            lock.lock();
            if (connectionPool != null) {
                throw new UnsupportedOperationException();
            } else {
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver()); // Регистрация драйвера базы данных
                init(); // Инициализация пула соединений
            }
        } finally {
            lock.unlock();
        }
    }

    private void init() {
        Properties properties = new Properties();
        ResourceBundle resource = ResourceBundle.getBundle(PROPERTY_PATH, Locale.getDefault()); // Загрузка настроек подключения к базе данных из файла свойств
        if (resource == null) {
            logger.error("Error while reading properties");
        } else {
            String connectionURL = resource.getString("db.url"); // Получение URL подключения из файла свойств
            String initialCapacityString = resource.getString("db.poolsize"); // Получение размера пула соединений из файла свойств
            String user = resource.getString("db.user"); // Получение имени пользователя из файла свойств
            String pass = resource.getString("db.password"); // Получение пароля из файла свойств
            Integer initialCapacity = Integer.valueOf(initialCapacityString);
            for (int i = 0; i < initialCapacity; i++) {
                try {
                    Connection connection = DriverManager.getConnection(connectionURL, user, pass); // Создание соединения с базой данных
                    freeConnections.add(connection); // Добавление соединения в очередь свободных соединений
                } catch (SQLException e) {
                    logger.error("Pool can not initialize", e);
                    throw new RuntimeException("Pool can not initialize", e);
                }
            }
        }
    }

    public Connection getConnection() {
        try {
            Connection connection = freeConnections.take(); // Извлечение соединения из очереди свободных соединений
            releaseConnections.offer(connection); // Добавление соединения в очередь взятых соединений
            logger.info("Connection was taken, the are free connection " + freeConnections.size());
            return connection;
        } catch (InterruptedException e) {
            throw new RuntimeException("Can not get database", e);
        }
    }

    public void releaseConnection(Connection connection) {
        releaseConnections.remove(connection); // Удаление соединения из очереди взятых соединений
        freeConnections.offer(connection); // Добавление соединения в очередь свободных соединений
        logger.info("Connection was released, the are free connection " + freeConnections.size());
    }

    public void destroy() {
        for (int i = 0; i < freeConnections.size(); i++) {
            try {
                Connection connection = (Connection) freeConnections.take();// Закрытие всех соединений из очереди свободных соединений
                connection.close();
            } catch (SQLException | InterruptedException e) {
                logger.error("Connection can not be closed", e);
            }
        }

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver); // Удаление драйвера базы данных из списка зарегистрированных драйверов
            } catch (SQLException e) {
                logger.error("Driver can not be deregistered", e);
            }
        }
    }
}
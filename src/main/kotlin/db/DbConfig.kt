package com.example.db

import com.zaxxer.hikari.HikariConfig
import io.github.cdimascio.dotenv.Dotenv
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import javax.sql.DataSource


object DbConfig {
    private val dotenv = Dotenv.load()
    private val logger = LoggerFactory.getLogger(DbConfig::class.java)

    private val dbHost = dotenv["DB_HOST"]
    private val dbPort = dotenv["DB_PORT"]
    private val dbName = dotenv["DB_NAME"]
    private val dbUser = dotenv["DB_USER"]
    private val dbPassword = dotenv["DB_PASSWORD"]

    private val connString = "jdbc:postgresql://$dbHost:$dbPort/$dbName"

    fun init() {
        try {
            logger.info("init connection to db: $connString")
            val dataSource = hikariDataSource()
            Database.connect(dataSource)

            logger.info("Make migration db")
            migrateDatabase(dataSource)
            logger.info("Migration made successful")

            checkTableExists(dataSource)
        } catch (e: Exception) {
            logger.error("Error while init db: ${e.message}", e)
        }

    }

    private fun hikariDataSource() :DataSource{
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = connString
            username = dbUser
            password = dbPassword
            maximumPoolSize = 10
        }
        return HikariDataSource(config)
    }

    private fun migrateDatabase(dataSource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .schemas("public")
            .baselineOnMigrate(true)
            .load()
        flyway.migrate()
    }

    private fun checkTableExists(dataSource: DataSource) {
        dataSource.connection.use { conn ->
            val statement = conn.prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'trading_pairs')"
            )
            val result = statement.executeQuery()
            if (result.next() && result.getBoolean(1)) {
                logger.info("table trading_pairs exists")
            } else {
                logger.error("Table trading_pairs wasn't created!")
            }
        }
    }
}
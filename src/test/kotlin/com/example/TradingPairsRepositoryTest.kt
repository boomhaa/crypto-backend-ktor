package com.example


import com.example.db.repositories.TradingPairsRepository
import com.example.db.tables.TradingPairsTable
import com.example.dto.PairInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.*
import kotlin.test.*

import java.math.BigDecimal
import java.time.Instant
import kotlin.test.Test


class TradingPairsRepositoryTest {

    private lateinit var repository: TradingPairsRepository

    companion object {
        private val testDbUrl = "jdbc:h2:mem:test_repo_${System.nanoTime()};DB_CLOSE_DELAY=-1"
        private val testDbDriver = "org.h2.Driver"

        @BeforeAll
        @JvmStatic
        fun initDatabaseConnection() {
            Database.connect(testDbUrl, driver = testDbDriver)
        }
    }

    @BeforeEach
    fun setup() {
        repository = TradingPairsRepository()

        transaction {
            SchemaUtils.drop(TradingPairsTable)
            SchemaUtils.create(TradingPairsTable)

            TradingPairsTable.insert {
                it[pair] = "BTCUSDT"
                it[baseAsset] = "BTC"
                it[quoteAsset] = "USDT"
                it[price] = BigDecimal("40000.50")
                it[lastUpdated] = Instant.now().minusSeconds(600)
            }
            TradingPairsTable.insert {
                it[pair] = "DOGEUSDT"
                it[baseAsset] = "DOGE"
                it[quoteAsset] = "USDT"
                it[price] = BigDecimal("0.15")
                it[lastUpdated] = Instant.now().minusSeconds(300)
            }
            TradingPairsTable.insert {
                it[pair] = "ETHBTC"
                it[baseAsset] = "ETH"
                it[quoteAsset] = "BTC"
                it[price] = BigDecimal("0.05")
                it[lastUpdated] = Instant.now().minusSeconds(3600)
            }
            TradingPairsTable.insert {
                it[pair] = "LTCBTC"
                it[baseAsset] = "LTC"
                it[quoteAsset] = "BTC"
                it[price] = BigDecimal("0.005")
                it[lastUpdated] = Instant.now().minusSeconds(7200)
            }
            TradingPairsTable.insert {
                it[pair] = "XRPETH"
                it[baseAsset] = "XRP"
                it[quoteAsset] = "ETH"
                it[price] = BigDecimal("0.0002")
                it[lastUpdated] = Instant.now().minusSeconds(10800)
            }
            TradingPairsTable.insert {
                it[pair] = "NOUSDTPRICE"
                it[baseAsset] = "NO"
                it[quoteAsset] = "USDT"
                it[lastUpdated] = Instant.now().minusSeconds(86400)
            }
            TradingPairsTable.insert {
                it[pair] = "CASETESTbtc"
                it[baseAsset] = "CT"
                it[quoteAsset] = "BTC"
                it[price] = BigDecimal("1.0")
                it[lastUpdated] = Instant.now().minusSeconds(14400)
            }
        }
    }

    @AfterEach
    fun tearDown() {
        transaction {
            SchemaUtils.drop(TradingPairsTable)
        }
    }

    @Test
    fun `findByQuery should return pairs containing query in pair (case-insensitive)`() {
        val query = "usdt"
        val resultUsdt = repository.findByQuery(query)

        assertEquals(3, resultUsdt.size, "Should find 3 pairs containing 'usdt'")
        assertTrue(resultUsdt.any { it.pair == "BTCUSDT" }, "BTCUSDT should be in results for 'usdt'")
        assertTrue(resultUsdt.any { it.pair == "DOGEUSDT" }, "DOGEUSDT should be in results for 'usdt'")
        assertTrue(resultUsdt.any { it.pair == "NOUSDTPRICE" }, "NOUSDTPRICE should be in results for 'usdt'")
        assertFalse(resultUsdt.any { it.pair == "ETHBTC" }, "ETHBTC should not be in results for 'usdt'")

        val resultUsdtCase = repository.findByQuery("UsDt")

        assertEquals(3, resultUsdtCase.size, "Should find 3 pairs containing 'UsDt' (case-insensitive)")
        assertTrue(resultUsdtCase.any { it.pair == "BTCUSDT" }, "BTCUSDT should be in results for 'UsDt'")
        assertTrue(resultUsdtCase.any { it.pair == "DOGEUSDT" }, "DOGEUSDT should be in results for 'UsDt'")
        assertTrue(resultUsdtCase.any { it.pair == "NOUSDTPRICE" }, "NOUSDTPRICE should be in results for 'UsDt'")

        val resultBtc = repository.findByQuery("btc")

        assertEquals(4, resultBtc.size, "Should find 4 pairs containing 'btc'")
        assertTrue(resultBtc.any { it.pair == "BTCUSDT" }, "BTCUSDT should be in results for 'btc'")
        assertTrue(resultBtc.any { it.pair == "ETHBTC" }, "ETHBTC should be in results for 'btc'")
        assertTrue(resultBtc.any { it.pair == "LTCBTC" }, "LTCBTC should be in results for 'btc'")
        assertTrue(resultBtc.any { it.pair == "CASETESTbtc" }, "CASETESTbtc should be in results for 'btc'")
        assertFalse(resultBtc.any { it.pair == "XRPETH" }, "XRPETH should not be in results for 'btc'")

        val resultOge = repository.findByQuery("oge")

        assertEquals(1, resultOge.size, "Should find 1 pair containing 'oge'")
        assertTrue(resultOge.any { it.pair == "DOGEUSDT" }, "DOGEUSDT should be in results for 'oge'")
        assertFalse(resultOge.any { it.pair == "BTCUSDT" }, "BTCUSDT should not be in results for 'oge'")

        val noUsdt = resultUsdt.first { it.pair == "NOUSDTPRICE" }

        assertEquals(null, noUsdt.price, "Price should be null for NOUSDTPRICE")
        assertTrue(noUsdt.lastUpdated != null, "lastUpdated should not be null for NOUSDTPRICE")

        val btcUsdt = resultUsdt.first { it.pair == "BTCUSDT" }

        assertEquals("40000.50000000", btcUsdt.price, "Price should match for BTCUSDT")
        assertTrue(btcUsdt.lastUpdated != null, "lastUpdated should not be null for BTCUSDT")
    }

    @Test
    fun `findByQuery should return empty list for no match`() {
        val query = "xyz"
        val result = repository.findByQuery(query)
        assertTrue(result.isEmpty(), "Result list should be empty for no match")
    }

    @Test
    fun `findByQuery should return all pairs for empty query`() {
        val query = ""
        val result = repository.findByQuery(query)

        assertEquals(7, result.size, "Result list size should match total number of pairs")
        assertTrue(result.any { it.pair == "BTCUSDT" }, "BTCUSDT should be present")
        assertTrue(result.any { it.pair == "DOGEUSDT" }, "DOGEUSDT should be present")
        assertTrue(result.any { it.pair == "ETHBTC" }, "ETHBTC should be present")
        assertTrue(result.any { it.pair == "LTCBTC" }, "LTCBTC should be present")
        assertTrue(result.any { it.pair == "XRPETH" }, "XRPETH should be present")
        assertTrue(result.any { it.pair == "NOUSDTPRICE" }, "NOUSDTPRICE should be present")
        assertTrue(result.any { it.pair == "CASETESTbtc" }, "CASETESTbtc should be present")
    }


    @Test
    fun `findAll should return all pairs`() {
        val result = repository.findAll()

        assertEquals(7, result.size, "findAll should return all 7 pairs")
        assertTrue(result.any { it.pair == "BTCUSDT" }, "BTCUSDT should be present in findAll result")
        assertTrue(result.any { it.pair == "DOGEUSDT" }, "DOGEUSDT should be present in findAll result")
        assertTrue(result.any { it.pair == "ETHBTC" }, "ETHBTC should be present in findAll result")
        assertTrue(result.any { it.pair == "LTCBTC" }, "LTCBTC should be present in findAll result")
        assertTrue(result.any { it.pair == "XRPETH" }, "XRPETH should be present in findAll result")
        assertTrue(result.any { it.pair == "NOUSDTPRICE" }, "NOUSDTPRICE should be present in findAll result")
        assertTrue(result.any { it.pair == "CASETESTbtc" }, "CASETESTbtc should be present in findAll result")

        val btcUsdt = result.first { it.pair == "BTCUSDT" }

        assertEquals("BTC", btcUsdt.baseAsset, "Base asset should match for BTCUSDT")
        assertEquals("USDT", btcUsdt.quoteAsset, "Quote asset should match for BTCUSDT")
        assertEquals("40000.50000000", btcUsdt.price, "Price should match for BTCUSDT")
        assertTrue(btcUsdt.lastUpdated != null, "lastUpdated should not be null for BTCUSDT")

        val noUsdtPrice = result.first { it.pair == "NOUSDTPRICE" }

        assertEquals(null, noUsdtPrice.price, "Price should be null for NOUSDTPRICE")
    }

    @Test
    fun `findPopular should return only popular pairs`() {
        val result = repository.findPopular()

        assertEquals(2, result.size, "Should return only 2 popular pairs from test data")
        assertTrue(result.any { it.pair == "BTCUSDT" }, "BTCUSDT should be popular")
        assertFalse(result.any { it.pair == "ETHBTC" }, "ETHBTC should not be popular")
        assertTrue(result.any { it.pair == "DOGEUSDT" }, "DOGEUSDT should be popular")
        assertFalse(result.any { it.pair == "LTCBTC" }, "LTCBTC should not be popular")
        assertFalse(result.any { it.pair == "XRPETH" }, "XRPETH should not be popular")
        assertFalse(result.any { it.pair == "NOUSDTPRICE" }, "NOUSDTPRICE should not be popular")
        assertFalse(result.any { it.pair == "CASETESTbtc" }, "CASETESTbtc should not be popular")

        val btcUsdt = result.first { it.pair == "BTCUSDT" }

        assertEquals("40000.50000000", btcUsdt.price, "Price should match for popular BTCUSDT")

        val ethBtc = result.first { it.pair == "DOGEUSDT" }

        assertEquals("0.15000000", ethBtc.price, "Price should match for popular ETHBTC")
    }

    @Test
    fun `findPopular should return empty list if no popular pairs exist in DB`() {
        transaction { SchemaUtils.drop(TradingPairsTable) }
        transaction { SchemaUtils.create(TradingPairsTable) }

        transaction {
            TradingPairsTable.insert {
                it[pair] = "NOTPOPULARUSDT"
                it[baseAsset] = "NP"
                it[quoteAsset] = "USDT"
                it[price] = BigDecimal("1.0")
                it[lastUpdated] = Instant.now()
            }
        }

        val result = repository.findPopular()

        assertTrue(result.isEmpty(), "Should return empty list when no popular pairs found")
    }

    @Test
    fun `saveAll should insert new pairs`() {
        transaction { SchemaUtils.drop(TradingPairsTable); SchemaUtils.create(TradingPairsTable) }

        val newPairs = listOf(
            PairInfo("NEWUSDT", "NEW", "USDT", "100.0", "dummy"),
            PairInfo("ANOTHERBTC", "ANOTHER", "BTC", null, "dummy")
        )

        repository.saveAll(newPairs)

        transaction {
            val allPairs = TradingPairsTable.selectAll().map { row ->
                PairInfo(
                    pair = row[TradingPairsTable.pair],
                    baseAsset = row[TradingPairsTable.baseAsset],
                    quoteAsset = row[TradingPairsTable.quoteAsset],
                    price = row[TradingPairsTable.price]?.toPlainString(),
                    lastUpdated = row[TradingPairsTable.lastUpdated].toString()
                )
            }
            assertEquals(2, allPairs.size, "Should insert 2 new pairs")
            assertTrue(allPairs.any { it.pair == "NEWUSDT" && it.baseAsset == "NEW" && it.quoteAsset == "USDT" && it.price == "100.00000000" }, "NEWUSDT should be inserted correctly")
            assertTrue(allPairs.any { it.pair == "ANOTHERBTC" && it.baseAsset == "ANOTHER" && it.quoteAsset == "BTC" && it.price == null }, "ANOTHERBTC should be inserted correctly with null price")
            assertTrue(allPairs.first { it.pair == "NEWUSDT" }.lastUpdated != null, "lastUpdated should be set for NEWUSDT")
            assertTrue(allPairs.first { it.pair == "ANOTHERBTC" }.lastUpdated != null, "lastUpdated should be set for ANOTHERBTC")
        }
    }

    @Test
    fun `saveAll should update existing pairs`() {
        val initialBtcUsdtLastUpdated = transaction {
            TradingPairsTable.select { TradingPairsTable.pair eq "BTCUSDT" }.single()[TradingPairsTable.lastUpdated]
        }

        val updatedPairs = listOf(
            PairInfo("BTCUSDT", "BTC", "USDT", "41000.75", "dummy"),
            PairInfo("ETHBTC", "ETH", "BTC", null, "dummy")
        )

        Thread.sleep(50)

        repository.saveAll(updatedPairs)

        transaction {
            val allPairs = TradingPairsTable.selectAll().map { row ->
                PairInfo(
                    pair = row[TradingPairsTable.pair],
                    baseAsset = row[TradingPairsTable.baseAsset],
                    quoteAsset = row[TradingPairsTable.quoteAsset],
                    price = row[TradingPairsTable.price]?.toPlainString(),
                    lastUpdated = row[TradingPairsTable.lastUpdated].toString()
                )
            }

            assertEquals(7, allPairs.size, "Total number of pairs should not change when only updating")

            val btcUsdt = allPairs.first { it.pair == "BTCUSDT" }
            assertEquals("BTC", btcUsdt.baseAsset, "Base asset should not change on update")
            assertEquals("USDT", btcUsdt.quoteAsset, "Quote asset should not change on update")
            assertEquals("41000.75000000", btcUsdt.price, "Price should be updated for BTCUSDT")
            val updatedBtcUsdtLastUpdated = transaction { TradingPairsTable.select { TradingPairsTable.pair eq "BTCUSDT" }.single()[TradingPairsTable.lastUpdated] }
            assertTrue(updatedBtcUsdtLastUpdated > initialBtcUsdtLastUpdated, "Last updated should be updated and be later for BTCUSDT")

            val ethBtc = allPairs.first { it.pair == "ETHBTC" }
            assertEquals("ETH", ethBtc.baseAsset)
            assertEquals("BTC", ethBtc.quoteAsset)
            assertEquals(null, ethBtc.price, "Price should be updated to null for ETHBTC")
            assertTrue(ethBtc.lastUpdated != null, "Last updated should be set for ETHBTC")

            val dogeUsdtBeforeRow = transaction { TradingPairsTable.select { TradingPairsTable.pair eq "DOGEUSDT" }.single() }
            val dogeUsdtAfter = allPairs.first { it.pair == "DOGEUSDT" }
            assertEquals(dogeUsdtBeforeRow[TradingPairsTable.price]?.toPlainString(), dogeUsdtAfter.price, "Price should remain unchanged for non-updated pairs")
            assertEquals(dogeUsdtBeforeRow[TradingPairsTable.lastUpdated].toString(), dogeUsdtAfter.lastUpdated, "Last updated should not change for non-updated pairs")
        }
    }

    @Test
    fun `saveAll should handle mixed new and existing pairs`() {
        val initialBtcUsdtPrice = transaction {
            TradingPairsTable.select { TradingPairsTable.pair eq "BTCUSDT" }.single()[TradingPairsTable.price]?.toPlainString()
        }
        val initialBtcUsdtLastUpdated = transaction {
            TradingPairsTable.select { TradingPairsTable.pair eq "BTCUSDT" }.single()[TradingPairsTable.lastUpdated]
        }

        val mixedPairs = listOf(
            PairInfo("BTCUSDT", "BTC", "USDT", "42000.0", "dummy"),
            PairInfo("NEWMIXUSDT", "NEWM", "USDT", "50.0", "dummy")
        )

        Thread.sleep(50)

        repository.saveAll(mixedPairs)

        transaction {
            val allPairs = TradingPairsTable.selectAll().map { row ->
                PairInfo(
                    pair = row[TradingPairsTable.pair],
                    baseAsset = row[TradingPairsTable.baseAsset],
                    quoteAsset = row[TradingPairsTable.quoteAsset],
                    price = row[TradingPairsTable.price]?.toPlainString(),
                    lastUpdated = row[TradingPairsTable.lastUpdated].toString()
                )
            }

            assertEquals(8, allPairs.size, "Total number of pairs should be 7 (initial) + 1 (new)")

            val btcUsdt = allPairs.first { it.pair == "BTCUSDT" }
            assertEquals("42000.00000000", btcUsdt.price, "Price should be updated for BTCUSDT")
            assertNotEquals(initialBtcUsdtPrice, btcUsdt.price, "BTCUSDT price should have been updated")
            val btcUsdtLastUpdated = transaction { TradingPairsTable.select { TradingPairsTable.pair eq "BTCUSDT" }.single()[TradingPairsTable.lastUpdated] }
            assertTrue(btcUsdtLastUpdated > initialBtcUsdtLastUpdated, "BTCUSDT last updated time should have changed")

            assertTrue(allPairs.any { it.pair == "NEWMIXUSDT" && it.baseAsset == "NEWM" && it.quoteAsset == "USDT" && it.price == "50.00000000" }, "NEWMIXUSDT should be inserted")
            assertTrue(allPairs.first { it.pair == "NEWMIXUSDT" }.lastUpdated != null, "lastUpdated should be set for NEWMIXUSDT")

            assertTrue(allPairs.any { it.pair == "DOGEUSDT" }, "Existing DOGEUSDT pair should still be present")
            assertTrue(allPairs.any { it.pair == "ETHBTC" }, "Existing ETHBTC pair should still be present")
        }
    }

    @Test
    fun `saveAll should handle empty list`() {
        val initialCount = transaction { TradingPairsTable.selectAll().count() }
        assertEquals(7, initialCount, "Initial number of pairs should be 7")

        repository.saveAll(emptyList())

        transaction {
            val allPairsAfter = TradingPairsTable.selectAll().count()
            assertEquals(initialCount, allPairsAfter, "Number of pairs should not change after saving empty list")
        }
    }
}

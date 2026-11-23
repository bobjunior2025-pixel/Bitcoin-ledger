package com.aistudio.bitcoinledger.btcxyz.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "wallet_account")
data class WalletAccountEntity(
  @PrimaryKey val id: Int = 1,
  val name: String,
  val pin: String,
  val balanceBtc: Double,
  val lastHourlyUpdate: Long
)

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0L,
  val type: String, // "SEND", "RECEIVE", "HOURLY_REWARD"
  val recipient: String,
  val amountBtc: Double,
  val amountUsd: Double,
  val amountBrl: Double,
  val status: String, // "CONCLUDED", "PENDING", "FAILED_BANK_RETENTION"
  val timestamp: Long,
  val note: String
)

@Dao
interface WalletDao {
  @Query("SELECT * FROM wallet_account WHERE id = 1")
  suspend fun getAccount(): WalletAccountEntity?

  @Query("SELECT * FROM wallet_account WHERE id = 1")
  fun observeAccount(): Flow<WalletAccountEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccount(account: WalletAccountEntity)

  @Query("UPDATE wallet_account SET balanceBtc = :newBalance, lastHourlyUpdate = :timestamp WHERE id = 1")
  suspend fun updateBalanceAndTimestamp(newBalance: Double, timestamp: Long)

  @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
  fun observeTransactions(): Flow<List<TransactionEntity>>

  @Insert
  suspend fun insertTransaction(transaction: TransactionEntity)

  @Query("UPDATE transactions SET status = :newStatus, note = :newNote WHERE id = :txId")
  suspend fun updateTransactionStatus(txId: Long, newStatus: String, newNote: String)

  @Query("SELECT * FROM transactions WHERE status = 'PENDING'")
  suspend fun getPendingTransactions(): List<TransactionEntity>
}

@Database(entities = [WalletAccountEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class WalletDatabase : RoomDatabase() {
  abstract fun walletDao(): WalletDao

  companion object {
    @Volatile private var INSTANCE: WalletDatabase? = null

    fun getDatabase(context: Context): WalletDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WalletDatabase::class.java,
          "bitcoin_ledger_database"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}

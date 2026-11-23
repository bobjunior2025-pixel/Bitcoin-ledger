package com.aistudio.bitcoinledger.btcxyz.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
  val sender: String = "Ledger Nano X",
  val recipient: String,
  val institution: String = "Bitcoin Network (SegWit)",
  val amountBtc: Double,
  val amountUsd: Double,
  val amountBrl: Double,
  val feeBtc: Double = 0.000012,
  val txHash: String = "",
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

  @Update
  suspend fun updateTransaction(transaction: TransactionEntity)

  @Delete
  suspend fun deleteTransaction(transaction: TransactionEntity)

  @Query("UPDATE transactions SET status = :newStatus, note = :newNote WHERE id = :txId")
  suspend fun updateTransactionStatus(txId: Long, newStatus: String, newNote: String)

  @Query("SELECT * FROM transactions WHERE status = 'PENDING'")
  suspend fun getPendingTransactions(): List<TransactionEntity>
}

@Database(entities = [WalletAccountEntity::class, TransactionEntity::class], version = 2, exportSchema = false)
abstract class WalletDatabase : RoomDatabase() {
  abstract fun walletDao(): WalletDao

  companion object {
    @Volatile private var INSTANCE: WalletDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transactions ADD COLUMN sender TEXT NOT NULL DEFAULT 'Ledger Nano X'")
        db.execSQL("ALTER TABLE transactions ADD COLUMN institution TEXT NOT NULL DEFAULT 'Bitcoin Network (SegWit)'")
        db.execSQL("ALTER TABLE transactions ADD COLUMN feeBtc REAL NOT NULL DEFAULT 0.000012")
        db.execSQL("ALTER TABLE transactions ADD COLUMN txHash TEXT NOT NULL DEFAULT ''")
      }
    }

    fun getDatabase(context: Context): WalletDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WalletDatabase::class.java,
          "bitcoin_ledger_database"
        )
          .addMigrations(MIGRATION_1_2)
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}

package br.com.neuberoliveira.finance.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.neuberoliveira.finance.model.entity.TransactionEntity
import br.com.neuberoliveira.finance.model.entity.dao.TransactionDao

@Database(entities = [TransactionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun transactionDao(): TransactionDao
}

var db: AppDatabase? = null
fun getDatabase(ctx: Context): AppDatabase {
  // val ctx:Context = getApplicationContext()
  if (db == null) {
    db = Room.databaseBuilder(ctx, AppDatabase::class.java, "finance-database-01")
      .allowMainThreadQueries() // TODO Remover isso e fazer do jeito certo
      .build()
  }
  
  return db as AppDatabase
}
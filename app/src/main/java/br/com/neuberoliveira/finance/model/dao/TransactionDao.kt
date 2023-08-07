package br.com.neuberoliveira.finance.model.entity.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.neuberoliveira.finance.model.entity.TransactionEntity

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions ORDER BY uid DESC")
  fun getAll(): List<TransactionEntity>
  
  @Insert
  fun add(transaction: TransactionEntity): Long
  
  @Update
  fun update(transaction: TransactionEntity)
  
  @Query("UPDATE transactions SET sync=:sync WHERE uid=:id")
  fun updateSync(id: Long, sync:Boolean)
  
  /*@Query("SELECT * FROM user WHERE uid IN (:userIds)")
  fun loadAllByIds(userIds: IntArray): List<User>*/
  
  /*@Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
  fun findByName(first: String, last: String): TransactionEntity*/
  
  
  /*@Delete
  fun delete(user: User)*/
}
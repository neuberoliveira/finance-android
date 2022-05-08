package br.com.neuberoliveira.finance.model.entity.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.neuberoliveira.finance.model.entity.TransactionEntity

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions ORDER BY uid DESC")
  fun getAll(): List<TransactionEntity>
  
  @Insert
  fun add(transaction: TransactionEntity)
  
  /*@Query("SELECT * FROM user WHERE uid IN (:userIds)")
  fun loadAllByIds(userIds: IntArray): List<User>*/
  
  /*@Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
  fun findByName(first: String, last: String): TransactionEntity*/
  
  
  /*@Delete
  fun delete(user: User)*/
}
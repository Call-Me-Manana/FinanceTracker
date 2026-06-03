package com.example.financetracker

import android.content.Context
import androidx.room.Room
import com.example.financetracker.data.AppDatabase
import com.example.financetracker.data.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "finance_database"
            )
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance

            // seed ДАННЫЕ (без callback!)
            CoroutineScope(Dispatchers.IO).launch {
                val dao = instance.categoryDao()

                if (dao.getCount() == 0) {
                    dao?.insert(Category(name="Другое", icon="\uD83D\uDCE6", isIncome = false))
                    dao?.insert(Category(name="Прочие доходы", icon="\uD83D\uDCB0", isIncome = true))
                    dao?.insert(Category(name="Еда", icon="🍔", isIncome = false))
                    dao?.insert(Category(name="Транспорт", icon="🚕", isIncome = false))
                    dao?.insert(Category(name="Зарплата", icon="💼", isIncome = true))
                    dao?.insert(Category(name="Развлечения", icon="🎬", isIncome = false))
                }
            }

            instance
        }
    }
}
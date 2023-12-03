package hr.algebra.moviemanager.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Movie::class], version = 2, exportSchema = true) // Version updated to num 2
@TypeConverters(DateConverter::class)
abstract class MovieDatabase : RoomDatabase(){

    abstract fun movieDao() : MovieDao;

    companion object {
        @Volatile private var INSTANCE: MovieDatabase? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(MovieDatabase::class.java) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it}
            }

        private fun buildDatabase(context: Context): MovieDatabase {
            //Comment needs to be null for old movies before the migration

            val MIGRATION_1_2: Migration = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE movies ADD COLUMN comment TEXT")
                }
            }

            return Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java,
                "movies.db"
            )
                .addMigrations(MIGRATION_1_2) // migration
                .build()
        }
    }
}
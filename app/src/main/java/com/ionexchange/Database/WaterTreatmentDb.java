package com.ionexchange.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.DiagnosticDataDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.Entity.DiagnosticDataEntity;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.Entity.UsermanagementEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;


@Database(entities = {InputConfigurationEntity.class, OutputConfigurationEntity.class,
        VirtualConfigurationEntity.class, TimerConfigurationEntity.class,
        DefaultLayoutConfigurationEntity.class, MainConfigurationEntity.class,
        KeepAliveCurrentEntity.class, DiagnosticDataEntity.class, UsermanagementEntity.class},
        version = 2, exportSchema = false)
@TypeConverters(Converters.class)

public abstract class WaterTreatmentDb extends RoomDatabase {

    public static volatile WaterTreatmentDb INSTANCE;

    static Migration migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("");
        }
    };

    public static WaterTreatmentDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WaterTreatmentDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WaterTreatmentDb.class, "WaterTreatmentDatabase")
                            .allowMainThreadQueries()
                            .addMigrations(migration_1_2)
                            .build();

                }
            }
        }
        return INSTANCE;
    }

    public abstract InputConfigurationDao inputConfigurationDao();

    public abstract OutputConfigurationDao outputConfigurationDao();

    public abstract VirtualConfigurationDao virtualConfigurationDao();

    public abstract TimerConfigurationDao timerConfigurationDao();

    public abstract UserManagementDao userManagementDao();

    public abstract DefaultLayoutConfigurationDao defaultLayoutConfigurationDao();

    public abstract MainConfigurationDao mainConfigurationDao();

    public abstract KeepAliveCurrentValueDao keepAliveCurrentValueDao();

    public abstract DiagnosticDataDao diagnosticDataDao();

}

package com.supervision.livraisons.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DeliveryDao_Impl implements DeliveryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DeliveryEntity> __insertionAdapterOfDeliveryEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDeliveryStatus;

  private final SharedSQLiteStatement __preparedStmtOfClearAllDeliveries;

  public DeliveryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDeliveryEntity = new EntityInsertionAdapter<DeliveryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `deliveries` (`id`,`clientName`,`address`,`phone`,`notes`,`status`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DeliveryEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getClientName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getClientName());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAddress());
        }
        if (entity.getPhone() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhone());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNotes());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getStatus());
        }
      }
    };
    this.__preparedStmtOfUpdateDeliveryStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE deliveries SET status = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllDeliveries = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM deliveries";
        return _query;
      }
    };
  }

  @Override
  public void insertDeliveries(final List<DeliveryEntity> deliveries) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDeliveryEntity.insert(deliveries);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDeliveryStatus(final String deliveryId, final String status) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDeliveryStatus.acquire();
    int _argIndex = 1;
    if (status == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, status);
    }
    _argIndex = 2;
    if (deliveryId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, deliveryId);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateDeliveryStatus.release(_stmt);
    }
  }

  @Override
  public void clearAllDeliveries() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllDeliveries.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAllDeliveries.release(_stmt);
    }
  }

  @Override
  public LiveData<List<DeliveryEntity>> getAllDeliveries() {
    final String _sql = "SELECT * FROM deliveries ORDER BY clientName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"deliveries"}, false, new Callable<List<DeliveryEntity>>() {
      @Override
      @Nullable
      public List<DeliveryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfClientName = CursorUtil.getColumnIndexOrThrow(_cursor, "clientName");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<DeliveryEntity> _result = new ArrayList<DeliveryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeliveryEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpClientName;
            if (_cursor.isNull(_cursorIndexOfClientName)) {
              _tmpClientName = null;
            } else {
              _tmpClientName = _cursor.getString(_cursorIndexOfClientName);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            _item = new DeliveryEntity(_tmpId,_tmpClientName,_tmpAddress,_tmpPhone,_tmpNotes,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

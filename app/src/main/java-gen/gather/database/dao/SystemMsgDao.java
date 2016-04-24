package gather.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import gather.database.bean.SystemMsg;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SYSTEM_MSG".
*/
public class SystemMsgDao extends AbstractDao<SystemMsg, Long> {

    public static final String TABLENAME = "SYSTEM_MSG";

    /**
     * Properties of entity SystemMsg.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property Type = new Property(3, String.class, "type", false, "TYPE");
        public final static Property Attribute = new Property(4, String.class, "attribute", false, "ATTRIBUTE");
        public final static Property CreatedAt = new Property(5, String.class, "createdAt", false, "CREATED_AT");
        public final static Property Readed = new Property(6, Boolean.class, "readed", false, "READED");
    };


    public SystemMsgDao(DaoConfig config) {
        super(config);
    }
    
    public SystemMsgDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SYSTEM_MSG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: userId
                "\"CONTENT\" TEXT," + // 2: content
                "\"TYPE\" TEXT," + // 3: type
                "\"ATTRIBUTE\" TEXT," + // 4: attribute
                "\"CREATED_AT\" TEXT," + // 5: createdAt
                "\"READED\" INTEGER);"); // 6: readed
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SYSTEM_MSG\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SystemMsg entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(4, type);
        }
 
        String attribute = entity.getAttribute();
        if (attribute != null) {
            stmt.bindString(5, attribute);
        }
 
        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(6, createdAt);
        }
 
        Boolean readed = entity.getReaded();
        if (readed != null) {
            stmt.bindLong(7, readed ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SystemMsg readEntity(Cursor cursor, int offset) {
        SystemMsg entity = new SystemMsg( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // attribute
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // createdAt
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0 // readed
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SystemMsg entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAttribute(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreatedAt(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setReaded(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SystemMsg entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SystemMsg entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

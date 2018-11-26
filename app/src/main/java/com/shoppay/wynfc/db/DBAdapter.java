package com.shoppay.wynfc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shoppay.wynfc.bean.NumShop;
import com.shoppay.wynfc.bean.ShopCar;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter {
    private volatile  static DBAdapter adapterInstance;
    private Context context;
    private DBHelper tvmDBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        tvmDBHelper = new DBHelper(context);

    }

    /**
     * return DBAdapter instance
     *
     * @param ctx
     * @return
     */
    public static synchronized DBAdapter getInstance(Context ctx) {
        if (adapterInstance == null) {
            synchronized (DBAdapter.class){
                if (adapterInstance==null){
                    adapterInstance = new DBAdapter(ctx);
                    adapterInstance.openWriteable();
                }
            }
        }
        return adapterInstance;
    }

    public void openWriteable() throws SQLException {
        db = tvmDBHelper.getWritableDatabase();
    }

    /**
     * close the database
     */
    public void close() {
        if (tvmDBHelper != null) {
            tvmDBHelper.close();
            tvmDBHelper = null;
            adapterInstance = null;
        }
    }


    /**
     * 更新购物车信息
     */
    public void updateShopCar(ShopCar shopcar, String shopid) {
        ContentValues initialValues = getShopCarValues(shopcar);

        int id = db.update(DBHelper.SHOP_TABLE_NAME, initialValues,
                DBHelper.shop_goodsid + " =?", new String[]{shopid});
    }


    public void updateListShopCar(List<ShopCar> list, String shopid) {
        for(ShopCar shopcar:list) {
            ContentValues initialValues = getShopCarValues(shopcar);

            int id = db.update(DBHelper.SHOP_TABLE_NAME, initialValues,
                    DBHelper.shop_goodsid + " =?", new String[]{shopid});
        }
    }


    public ContentValues getShopCarValues(ShopCar shopcar) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(DBHelper.shop_count, shopcar.count);
        initialValues.put(DBHelper.shop_discount, shopcar.discount);
        initialValues.put(DBHelper.shop_discountmoney, shopcar.discountmoney);
        initialValues.put(DBHelper.shop_goodsid, shopcar.goodsid);
        initialValues.put(DBHelper.shop_goodspoint, shopcar.goodspoint);
        initialValues.put(DBHelper.shop_shopname, shopcar.shopname);
        initialValues.put(DBHelper.shop_classid, shopcar.goodsclassid);
        initialValues.put(DBHelper.shop_goodsType, shopcar.goodsType);
        initialValues.put(DBHelper.shop_point, shopcar.point);
        initialValues.put(DBHelper.shop_pointPercent, shopcar.pointPercent);
        initialValues.put(DBHelper.shop_price, shopcar.price);
        initialValues.put(DBHelper.shop_handler, shopcar.account);
        return initialValues;
    }

    /**
     * 根据账号获取商品信息
     *
     * @return
     */
    public List<ShopCar> getLiShopCar(Cursor cursor) {
        List<ShopCar> list = null;
        if (cursor != null) {
            list = new ArrayList<ShopCar>();
            while (cursor.moveToNext()) {
                ShopCar shopcar = new ShopCar();

                shopcar.count = cursor.getInt(cursor
                        .getColumnIndex(DBHelper.shop_count));
                shopcar.discount = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_discount));
                shopcar.shopname = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_shopname));
                shopcar.discountmoney = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_discountmoney));
                shopcar.goodsid = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_goodsid));
                shopcar.goodspoint = cursor.getInt(cursor
                        .getColumnIndex(DBHelper.shop_goodspoint));
                shopcar.goodsType = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_goodsType));
                shopcar.goodsclassid = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_classid));
                shopcar.point = cursor.getInt(cursor
                        .getColumnIndex(DBHelper.shop_point));
                shopcar.pointPercent = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_pointPercent));
                shopcar.price = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_price));
                shopcar.account = cursor.getString(cursor
                        .getColumnIndex(DBHelper.shop_handler));
                list.add(shopcar);
            }

            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据账号获取商品信息
     *
     * @return
     */
    public List<ShopCar> getListShopCar(String account) {
        Cursor cursor = null;
        cursor = db.query(DBHelper.SHOP_TABLE_NAME, null,
                DBHelper.shop_handler + " =?", new String[]{account.trim()},
                null, null, null);

        return getLiShopCar(cursor);
    }
    //
    //
    // public List<ShopCar> getListJpushMessage(String num, String CommunityId,
    // String type, int pageID, int PageSize) {
    // Cursor cursor = null;
    // StringBuffer sb = new StringBuffer();
    // sb.append(type);
    // sb.append(" AND ");
    //
    // sb.append(DBHelper.jpush_phone + "=").append("'").append(num +
    // "' order by " + DBHelper.jpush_time + " desc ");
    // // sb.append(" AND ");
    // // sb.append(DBHelper.jpush_comcode +
    // "=").append("'").append(CommunityCode + "' order by " +
    // DBHelper.jpush_time + " desc ");
    // sb.append("Limit").append("'").append(String.valueOf(PageSize) +
    // "' ").append("Offset").append("'").append(String.valueOf(pageID *
    // PageSize) + "' ");
    // cursor = db.rawQuery("SELECT * FROM " + DBHelper.ShopCar_TABLE_NAME +
    // " WHERE " + sb
    // .toString().trim(), null);
    //
    // return getListJpushMessage(cursor);
    // }
    //
     /**
     * 根据商品id获取购物车商品信息
     *
     * @return
     */
     public ShopCar getShopCar(String number) {
     ShopCar shopcar = null;
     Cursor cursor = null;
     if (!"".equals(number)) {
     cursor = db.query(DBHelper.SHOP_TABLE_NAME, null,
     DBHelper.shop_goodsid + "=?", new String[]{number}, null,
     null, null);
     Log.d("find", number);
     if (cursor.moveToFirst()) {
     shopcar = new ShopCar();
         shopcar.count = cursor.getInt(cursor
                 .getColumnIndex(DBHelper.shop_count));
         shopcar.discount = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_discount));
         shopcar.shopname = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_shopname));
         shopcar.discountmoney = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_discountmoney));
         shopcar.goodsid = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_goodsid));
         shopcar.goodspoint = cursor.getInt(cursor
                 .getColumnIndex(DBHelper.shop_goodspoint));
         shopcar.goodsType = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_goodsType));
         shopcar.goodsclassid = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_classid));
         shopcar.point = cursor.getInt(cursor
                 .getColumnIndex(DBHelper.shop_point));
         shopcar.pointPercent = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_pointPercent));
         shopcar.price = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_price));
         shopcar.account = cursor.getString(cursor
                 .getColumnIndex(DBHelper.shop_handler));
     }
     }
     if (!cursor.isClosed()) {
     cursor.close();
     }
     return shopcar;
     }
    //
     /**
     * 将所有获取到的卷烟信息保存到数据库中
     *
     * @return
     */
     public long insertShopCar(List<ShopCar> list) {
     // deleteLABEL_TABLE_NAME();
     int count = 0;
     if (null != list && list.size() > 0) {
     db.beginTransaction();
     ContentValues initialValues = null;
     for (ShopCar shopcar : list) {
         if(hasShopCar(shopcar.goodsid)){
             updateShopCar(shopcar,shopcar.goodsid);
         }else {
             initialValues = new ContentValues();
             initialValues.put(DBHelper.shop_count, shopcar.count);
             initialValues.put(DBHelper.shop_discount, shopcar.discount);
             initialValues.put(DBHelper.shop_discountmoney, shopcar.discountmoney);
             initialValues.put(DBHelper.shop_goodsid, shopcar.goodsid);
             initialValues.put(DBHelper.shop_shopname, shopcar.shopname);
             initialValues.put(DBHelper.shop_goodspoint, shopcar.goodspoint);
             initialValues.put(DBHelper.shop_goodsType, shopcar.goodsType);
             initialValues.put(DBHelper.shop_classid, shopcar.goodsclassid);
             initialValues.put(DBHelper.shop_point, shopcar.point);
             initialValues.put(DBHelper.shop_pointPercent, shopcar.pointPercent);
             initialValues.put(DBHelper.shop_price, shopcar.price);
             initialValues.put(DBHelper.shop_handler, shopcar.account);
             db.insert(DBHelper.SHOP_TABLE_NAME, null, initialValues);
         }
     }
     db.setTransactionSuccessful();
     db.endTransaction();
     }
     Log.i("tag", list.size() + "=总共成度。. :");
     return count;
     }


    public long insertNotUpShopCar(List<ShopCar> list) {
        // deleteLABEL_TABLE_NAME();
        int count = 0;
        if (null != list && list.size() > 0) {
            db.beginTransaction();
            ContentValues initialValues = null;
            for (ShopCar shopcar : list) {
//                if(hasShopCar(shopcar.goodsid)){
//                    updateShopCar(shopcar,shopcar.goodsid);
//                }else {
                    initialValues = new ContentValues();
                    initialValues.put(DBHelper.shop_count, shopcar.count);
                    initialValues.put(DBHelper.shop_discount, shopcar.discount);
                    initialValues.put(DBHelper.shop_discountmoney, shopcar.discountmoney);
                    initialValues.put(DBHelper.shop_goodsid, shopcar.goodsid);
                    initialValues.put(DBHelper.shop_shopname, shopcar.shopname);
                    initialValues.put(DBHelper.shop_goodspoint, shopcar.goodspoint);
                    initialValues.put(DBHelper.shop_goodsType, shopcar.goodsType);
                    initialValues.put(DBHelper.shop_classid, shopcar.goodsclassid);
                    initialValues.put(DBHelper.shop_point, shopcar.point);
                    initialValues.put(DBHelper.shop_pointPercent, shopcar.pointPercent);
                    initialValues.put(DBHelper.shop_price, shopcar.price);
                    initialValues.put(DBHelper.shop_handler, shopcar.account);
                    db.insert(DBHelper.SHOP_TABLE_NAME, null, initialValues);
//                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        Log.i("tag", list.size() + "=总共成度。. :");
        return count;
    }
     public Boolean hasShopCar(String number) {
     Boolean b = false;
     Cursor cursor = null;
     if (!"".equals(number)) {
     cursor = db.query(DBHelper.SHOP_TABLE_NAME, null,
     DBHelper.shop_goodsid + "=?", new String[]{number},
     null, null, null);
     b = cursor.moveToFirst();
     }
     if (!cursor.isClosed()) {
     cursor.close();
     }
     return b;
     }
     public boolean deleteShopCar() {
     return db.delete(DBHelper.SHOP_TABLE_NAME, null, null) > 0;
     }















    /**
     * 更新购物车信息
     */
    public void updateNumShopCar(NumShop shopcar, String shopid) {
        ContentValues initialValues = getNumShopCarValues(shopcar);

        int id = db.update(DBHelper.NUMSHOP_TABLE_NAME, initialValues,
                DBHelper.numhop_CountDetailGoodsID + " =?", new String[]{shopid});
    }



    public ContentValues getNumShopCarValues(NumShop shopcar) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBHelper.numshop_account, shopcar.account);
        initialValues.put(DBHelper.numhop_CountDetailGoodsID, shopcar.CountDetailGoodsID);
        initialValues.put(DBHelper.numshop_allnum, shopcar.allnum);
        initialValues.put(DBHelper.numshop_shopname, shopcar.shopname);
        initialValues.put(DBHelper.numshop_count, shopcar.count+"");
        return initialValues;
    }

    /**
     * 根据账号获取商品信息
     *
     * @return
     */
    public List<NumShop> getLiNumShopCar(Cursor cursor) {
        List<NumShop> list = null;
        if (cursor != null) {
            list = new ArrayList<NumShop>();
            while (cursor.moveToNext()) {
                NumShop shopcar = new NumShop();

                shopcar.count =Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_count)));
                shopcar.CountDetailGoodsID = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numhop_CountDetailGoodsID));
                shopcar.allnum = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_allnum));
                shopcar.shopname = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_shopname));
                shopcar.account = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_account));
                list.add(shopcar);
            }

            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据账号获取商品信息
     *
     * @return
     */
    public List<NumShop> getListNumShopCar(String account) {
        Cursor cursor = null;
        cursor = db.query(DBHelper.NUMSHOP_TABLE_NAME, null,
                DBHelper.numshop_account + " =?", new String[]{account.trim()},
                null, null, null);

        return getLiNumShopCar(cursor);
    }
    //
    //
    // public List<ShopCar> getListJpushMessage(String num, String CommunityId,
    // String type, int pageID, int PageSize) {
    // Cursor cursor = null;
    // StringBuffer sb = new StringBuffer();
    // sb.append(type);
    // sb.append(" AND ");
    //
    // sb.append(DBHelper.jpush_phone + "=").append("'").append(num +
    // "' order by " + DBHelper.jpush_time + " desc ");
    // // sb.append(" AND ");
    // // sb.append(DBHelper.jpush_comcode +
    // "=").append("'").append(CommunityCode + "' order by " +
    // DBHelper.jpush_time + " desc ");
    // sb.append("Limit").append("'").append(String.valueOf(PageSize) +
    // "' ").append("Offset").append("'").append(String.valueOf(pageID *
    // PageSize) + "' ");
    // cursor = db.rawQuery("SELECT * FROM " + DBHelper.ShopCar_TABLE_NAME +
    // " WHERE " + sb
    // .toString().trim(), null);
    //
    // return getListJpushMessage(cursor);
    // }
    //
    /**
     * 根据商品id获取购物车商品信息
     *
     * @return
     */
    public NumShop getNumShop(String number) {
        NumShop shopcar = null;
        Cursor cursor = null;
        if (!"".equals(number)) {
            cursor = db.query(DBHelper.NUMSHOP_TABLE_NAME, null,
                    DBHelper.numhop_CountDetailGoodsID + "=?", new String[]{number}, null,
                    null, null);
            Log.d("find", number);
            if (cursor.moveToFirst()) {
                shopcar = new NumShop();
                shopcar.count =Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_count)));
                shopcar.CountDetailGoodsID = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numhop_CountDetailGoodsID));
                shopcar.allnum = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_allnum));
                shopcar.shopname = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_shopname));
                shopcar.account = cursor.getString(cursor
                        .getColumnIndex(DBHelper.numshop_account));
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return shopcar;
    }
    //
    /**
     * 将所有获取到的卷烟信息保存到数据库中
     *
     * @return
     */
    public long insertNumShopCar(List<NumShop> list) {
        // deleteLABEL_TABLE_NAME();
        int count = 0;
        if (null != list && list.size() > 0) {
            db.beginTransaction();
            ContentValues initialValues = null;
            for (NumShop shopcar : list) {
                if(hasNumShopCar(shopcar.CountDetailGoodsID)){
                    updateNumShopCar(shopcar,shopcar.CountDetailGoodsID);
                }else {
                    initialValues = new ContentValues();
                    initialValues.put(DBHelper.numshop_account, shopcar.account);
                    initialValues.put(DBHelper.numhop_CountDetailGoodsID, shopcar.CountDetailGoodsID);
                    initialValues.put(DBHelper.numshop_allnum, shopcar.allnum);
                    initialValues.put(DBHelper.numshop_shopname, shopcar.shopname);
                    initialValues.put(DBHelper.numshop_count, shopcar.count+"");
                    db.insert(DBHelper.NUMSHOP_TABLE_NAME, null, initialValues);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        Log.i("tag", list.size() + "=总共成度。. :");
        return count;
    }
    public Boolean hasNumShopCar(String number) {
        Boolean b = false;
        Cursor cursor = null;
        if (!"".equals(number)) {
            cursor = db.query(DBHelper.NUMSHOP_TABLE_NAME, null,
                    DBHelper.numhop_CountDetailGoodsID + "=?", new String[]{number},
                    null, null, null);
            b = cursor.moveToFirst();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return b;
    }
    public boolean deleteNumShopCar() {
        return db.delete(DBHelper.NUMSHOP_TABLE_NAME, null, null) > 0;
    }









    //
    // // /车辆消息中心
    //
    // /**
    // * 根据number查询是该需要的信息
    // *
    // * @return
    // */
    // public List<CarInOrOut> getListCarMessage(Cursor cursor) {
    // List<CarInOrOut> list = null;
    // if (cursor != null) {
    // list = new ArrayList<CarInOrOut>();
    // while (cursor.moveToNext()) {
    // CarInOrOut shopcar = new CarInOrOut();
    //
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmsg_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmsg_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmsg_Msg));
    // list.add(shopcar);
    // }
    //
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // }
    // return list;
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 手机号
    // * @return
    // */
    // public List<CarInOrOut> getLiCarMessage(String num) {
    // Cursor cursor = null;
    // cursor = db.query(DBHelper.CARMESSAGE_TABLE_NAME, null,
    // DBHelper.carmsg_number + " =?", new String[]{num.trim()},
    // null, null, null);
    //
    // return getListCarMessage(cursor);
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 车牌号
    // * @return
    // */
    // public CarInOrOut getCarMessage(String number) {
    // CarInOrOut shopcar = null;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CARMESSAGE_TABLE_NAME, null,
    // DBHelper.carmsg_number + "=?", new String[]{number},
    // null, null, null);
    // Log.d("find", number);
    // if (cursor.moveToFirst()) {
    // shopcar = new CarInOrOut();
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmsg_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmsg_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmsg_Msg));
    // }
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return shopcar;
    // }
    //
    // /**
    // * 将所有获取到的卷烟信息保存到数据库中
    // *
    // * @param shopcarList 卷烟列表
    // * @return
    // */
    // public long insertCarMessage(List<CarInOrOut> list) {
    // // deleteLABEL_TABLE_NAME();
    // int count = 0;
    // if (null != list && list.size() > 0) {
    // db.beginTransaction();
    // ContentValues initialValues = null;
    // for (CarInOrOut shopcar : list) {
    // initialValues = new ContentValues();
    // initialValues.put(DBHelper.carmsg_number, shopcar.carNum);
    // initialValues.put(DBHelper.carmsg_phone, shopcar.pnone);
    // initialValues.put(DBHelper.carmsg_Msg, shopcar.Msg);
    // db.insert(DBHelper.CARMESSAGE_TABLE_NAME, null, initialValues);
    // }
    // // Log.i("tag", shopcar.number + "=添加 car信息成功。. :");
    // // }
    // db.setTransactionSuccessful();
    // db.endTransaction();
    // }
    // Log.i("tag", list.size() + "=总共成度。. :");
    // return count;
    // }
    //
    // public ContentValues getCarMessageValues(CarInOrOut shopcar) {
    // ContentValues initialValues = new ContentValues();
    //
    // initialValues.put(DBHelper.carmsg_Msg, shopcar.Msg);
    // initialValues.put(DBHelper.carmsg_number, shopcar.carNum);
    // initialValues.put(DBHelper.carmsg_phone, shopcar.pnone);
    //
    // return initialValues;
    // }
    //
    // public boolean deletecarMessage(String num) {
    // return db.delete(DBHelper.CARMESSAGE_TABLE_NAME, DBHelper.carmsg_number
    // + "=?", new String[]{num}) > 0;
    // }
    //
    // // /车辆缴费
    //
    // /**
    // * 根据number查询是该需要的信息
    // *
    // * @return
    // */
    // public List<CarInOrOut> getListCarZhifu(Cursor cursor) {
    // List<CarInOrOut> list = null;
    // if (cursor != null) {
    // list = new ArrayList<CarInOrOut>();
    // while (cursor.moveToNext()) {
    // CarInOrOut shopcar = new CarInOrOut();
    //
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carzhifu_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carzhifu_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carzhifu_Msg));
    // list.add(shopcar);
    // }
    //
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // }
    // return list;
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 手机号
    // * @return
    // */
    // public List<CarInOrOut> getLiCarZhifu(String num) {
    // Cursor cursor = null;
    // cursor = db.query(DBHelper.CARZHIFU_TABLE_NAME, null,
    // DBHelper.carzhifu_number + " =?", new String[]{num.trim()},
    // null, null, null);
    //
    // return getListCarZhifu(cursor);
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 车牌号
    // * @return
    // */
    // public CarInOrOut getCarZhifu(String number) {
    // CarInOrOut shopcar = null;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CARZHIFU_TABLE_NAME, null,
    // DBHelper.carzhifu_number + "=?", new String[]{number},
    // null, null, null);
    // Log.d("find", number);
    // if (cursor.moveToFirst()) {
    // shopcar = new CarInOrOut();
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carzhifu_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carzhifu_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carzhifu_Msg));
    // }
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return shopcar;
    // }
    //
    // /**
    // * 将所有获取到的卷烟信息保存到数据库中
    // *
    // * @param shopcarList 卷烟列表
    // * @return
    // */
    // public long insertCarZhifu(List<CarInOrOut> list) {
    // // deleteLABEL_TABLE_NAME();
    // int count = 0;
    // if (null != list && list.size() > 0) {
    // db.beginTransaction();
    // ContentValues initialValues = null;
    // for (CarInOrOut shopcar : list) {
    // if (hasCarZhifuMsg(shopcar.carNum)) {
    // updateCarZhifu(shopcar);
    // } else {
    // initialValues = new ContentValues();
    // initialValues.put(DBHelper.carzhifu_number,
    // shopcar.carNum);
    // initialValues.put(DBHelper.carzhifu_phone, shopcar.pnone);
    // initialValues.put(DBHelper.carzhifu_Msg, shopcar.Msg);
    // db.insert(DBHelper.CARZHIFU_TABLE_NAME, null, initialValues);
    // }
    // }
    // // Log.i("tag", shopcar.number + "=添加 car信息成功。. :");
    // // }
    // db.setTransactionSuccessful();
    // db.endTransaction();
    // }
    // Log.i("tag", list.size() + "=总共成度。. :");
    // return count;
    // }
    //
    // public void updateCarZhifu(CarInOrOut shopcar) {
    // ContentValues initialValues = getCarZhifuValues(shopcar);
    //
    // int id = db.update(DBHelper.CARZHIFU_TABLE_NAME, initialValues,
    // DBHelper.carzhifu_number + " =" + "'" + shopcar.carNum + "'",
    // null);
    // // Log.v("tag", shopcar.number + "=更新标签成功。。。");
    // }
    //
    // public Boolean hasCarZhifuMsg(String number) {
    // Boolean b = false;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CARZHIFU_TABLE_NAME, null,
    // DBHelper.carzhifu_number + "=?", new String[]{number},
    // null, null, null);
    // b = cursor.moveToFirst();
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return b;
    // }
    //
    // public ContentValues getCarZhifuValues(CarInOrOut shopcar) {
    // ContentValues initialValues = new ContentValues();
    //
    // initialValues.put(DBHelper.carzhifu_Msg, shopcar.Msg);
    // initialValues.put(DBHelper.carzhifu_number, shopcar.carNum);
    // initialValues.put(DBHelper.carzhifu_phone, shopcar.pnone);
    //
    // return initialValues;
    // }
    //
    // public boolean deletecarzhifu() {
    // Log.d("zhifubao", "删除欠费记录");
    // return db.delete(DBHelper.CARZHIFU_TABLE_NAME, null, null) > 0;
    // }
    //
    // public void deletecarzhifuOne(String carNum) {
    // db.delete(DBHelper.CARZHIFU_TABLE_NAME,
    // DBHelper.carzhifu_number + "=?", new String[]{carNum});
    // }
    //
    // // /车辆进出
    //
    // /**
    // * 根据number查询是该需要的信息
    // *
    // * @return
    // */
    // public List<CarInOrOut> getListCarInOrOutMsg(Cursor cursor) {
    // List<CarInOrOut> list = null;
    // if (cursor != null) {
    // list = new ArrayList<CarInOrOut>();
    // while (cursor.moveToNext()) {
    // CarInOrOut shopcar = new CarInOrOut();
    //
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.cario_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.cario_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.cario_Msg));
    // list.add(shopcar);
    // }
    //
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // }
    // return list;
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 手机号
    // * @return
    // */
    // public List<CarInOrOut> getLiCarInOrOuts(String num) {
    // Cursor cursor = null;
    // cursor = db.query(DBHelper.CARINOROUT_TABLE_NAME, null,
    // DBHelper.cario_phone + " =?", new String[]{num.trim()},
    // null, null, null);
    //
    // return getListCarInOrOutMsg(cursor);
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 车牌号
    // * @return
    // */
    // public CarInOrOut getCarInOrOut(String number) {
    // CarInOrOut shopcar = null;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CARINOROUT_TABLE_NAME, null,
    // DBHelper.cario_number + "=?", new String[]{number},
    // null, null, null);
    // Log.d("find", number);
    // if (cursor.moveToFirst()) {
    // shopcar = new CarInOrOut();
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.cario_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.cario_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.cario_Msg));
    // }
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return shopcar;
    // }
    //
    // /**
    // * 将所有获取到的卷烟信息保存到数据库中
    // *
    // * @param shopcarList 卷烟列表
    // * @return
    // */
    // public long insertCarInOrOutMsg(List<CarInOrOut> list) {
    // // deleteLABEL_TABLE_NAME();
    // int count = 0;
    // if (null != list && list.size() > 0) {
    // db.beginTransaction();
    // ContentValues initialValues = null;
    // for (CarInOrOut shopcar : list) {
    // initialValues = new ContentValues();
    // initialValues.put(DBHelper.cario_number, shopcar.carNum);
    // initialValues.put(DBHelper.cario_phone, shopcar.pnone);
    // initialValues.put(DBHelper.cario_Msg, shopcar.Msg);
    // db.insert(DBHelper.CARINOROUT_TABLE_NAME, null, initialValues);
    // }
    // // Log.i("tag", shopcar.number + "=添加 car信息成功。. :");
    // // }
    // db.setTransactionSuccessful();
    // db.endTransaction();
    // }
    // Log.i("tag", list.size() + "=总共成度。. :");
    // return count;
    // }
    //
    // public ContentValues getCarInorOutValues(CarInOrOut shopcar) {
    // ContentValues initialValues = new ContentValues();
    //
    // initialValues.put(DBHelper.cario_Msg, shopcar.Msg);
    // initialValues.put(DBHelper.cario_number, shopcar.carNum);
    // initialValues.put(DBHelper.cario_phone, shopcar.pnone);
    //
    // return initialValues;
    // }
    //
    // public boolean deletecarinorout() {
    // return db.delete(DBHelper.CARINOROUT_TABLE_NAME, null, null) > 0;
    // }
    //
    //
    // // 车辆信息数据库
    //
    // /**
    // * 根据number查询是该需要的信息
    // *
    // * @return
    // */
    // public List<Cars> getListCarInfoMsg(Cursor cursor) {
    // List<Cars> list = null;
    // if (cursor != null) {
    // list = new ArrayList<Cars>();
    // while (cursor.moveToNext()) {
    // Cars shopcar = new Cars();
    //
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_number));
    // shopcar.CarshopcarGuid = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_CarshopcarGuid));
    // shopcar.phone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_phone));
    // shopcar.isGuding = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_IsGuding));
    // shopcar.codeNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_monthcard));
    // shopcar.isHolder = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_IsHolder));
    // shopcar.isSuoche = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_IsSuoche));
    // list.add(shopcar);
    // }
    //
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // }
    // return list;
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 手机号
    // * @return
    // */
    // public List<Cars> getCar(String phone) {
    // Cursor cursor = null;
    // cursor = db.query(DBHelper.CAR_TABLE_NAME, null, DBHelper.car_phone
    // + " =?", new String[]{phone.trim()}, null, null, null);
    //
    // return getListCarInfoMsg(cursor);
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 车牌号
    // * @return
    // */
    // public Cars getCars(String number) {
    // Cars shopcar = null;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CAR_TABLE_NAME, null,
    // DBHelper.car_number + "=?", new String[]{number}, null,
    // null, null);
    // Log.d("find", number);
    // if (cursor.moveToFirst()) {
    // shopcar = new Cars();
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_number));
    // shopcar.CarshopcarGuid = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_CarshopcarGuid));
    // shopcar.codeNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_monthcard));
    // shopcar.phone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_phone));
    // shopcar.isHolder = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_IsHolder));
    // shopcar.isGuding = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_IsGuding));
    // shopcar.isSuoche = cursor.getString(cursor
    // .getColumnIndex(DBHelper.car_IsSuoche));
    // }
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return shopcar;
    // }
    //
    // /**
    // * 将所有获取到的卷烟信息保存到数据库中
    // *
    // * @param shopcarList 卷烟列表
    // * @return
    // */
    // public long insertCarInfo(List<Cars> list) {
    // // deleteLABEL_TABLE_NAME();
    // int count = 0;
    // if (null != list && list.size() > 0) {
    // db.beginTransaction();
    // for (Cars shopcar : list) {
    // if (hasCarInfoItemMsg(shopcar.carNum)) {
    // updateCarInfo(shopcar);
    // } else {
    // ContentValues initialValues = null;
    // initialValues = new ContentValues();
    // initialValues.put(DBHelper.car_CarshopcarGuid,
    // shopcar.CarshopcarGuid);
    // initialValues.put(DBHelper.car_IsHolder,
    // shopcar.isHolder);
    // initialValues.put(DBHelper.car_IsSuoche,
    // shopcar.isSuoche);
    // initialValues.put(DBHelper.car_IsGuding,
    // shopcar.isGuding);
    // initialValues
    // .put(DBHelper.car_monthcard, shopcar.codeNum);
    // initialValues.put(DBHelper.car_number, shopcar.carNum);
    // initialValues.put(DBHelper.car_phone, shopcar.phone);
    // db.insert(DBHelper.CAR_TABLE_NAME, null, initialValues);
    // }
    // // Log.i("tag", shopcar.number + "=添加 car信息成功。. :");
    // // }
    // }
    // db.setTransactionSuccessful();
    // db.endTransaction();
    // }
    // Log.i("tag", list.size() + "=总共成度。. :");
    // return count;
    // }
    //
    // // /**
    // // * 根据number查询货车是否存在
    // // *
    // // * @param first_letter
    // // * @param letter
    // // * @return
    // // */
    // public Boolean hasCarInfoItemMsg(String number) {
    // Boolean b = false;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CAR_TABLE_NAME, null,
    // DBHelper.car_number + "=?", new String[]{number}, null,
    // null, null);
    // b = cursor.moveToFirst();
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return b;
    // }
    //
    // /**
    // * 更新车辆信息
    // */
    // public void updateCarInfo(Cars shopcar) {
    // ContentValues initialValues = getCarinfoValues(shopcar);
    //
    // int id = db.update(DBHelper.CAR_TABLE_NAME, initialValues,
    // DBHelper.car_number + " =" + "'" + shopcar.carNum + "'", null);
    // // Log.v("tag", shopcar.number + "=更新标签成功。。。");
    // }
    //
    // public ContentValues getCarinfoValues(Cars shopcar) {
    // ContentValues initialValues = new ContentValues();
    //
    // initialValues.put(DBHelper.car_CarshopcarGuid, shopcar.CarshopcarGuid);
    // initialValues.put(DBHelper.car_monthcard, shopcar.codeNum);
    // initialValues.put(DBHelper.car_number, shopcar.carNum);
    // initialValues.put(DBHelper.car_phone, shopcar.phone);
    // initialValues.put(DBHelper.car_IsGuding, shopcar.isGuding);
    // initialValues.put(DBHelper.car_IsSuoche, shopcar.isSuoche);
    // initialValues.put(DBHelper.car_IsHolder, shopcar.isHolder);
    //
    // return initialValues;
    // }
    //
    // public void deleteCar(String num) {
    // db.delete(DBHelper.CAR_TABLE_NAME, DBHelper.car_number + "=?",
    // new String[]{num});
    // }
    //
    // public void deleteAllCar() {
    // db.delete(DBHelper.CAR_TABLE_NAME, null, null);
    // }
    // // /车辆欠费
    //
    // /**
    // * 根据number查询是该需要的信息
    // *
    // * @return
    // */
    // public List<CarInOrOut> getListCarMoney(Cursor cursor) {
    // List<CarInOrOut> list = null;
    // if (cursor != null) {
    // list = new ArrayList<CarInOrOut>();
    // while (cursor.moveToNext()) {
    // CarInOrOut shopcar = new CarInOrOut();
    //
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmoney_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmoney_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmoney_Msg));
    // list.add(shopcar);
    // }
    //
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // }
    // return list;
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 手机号
    // * @return
    // */
    // public List<CarInOrOut> getLiCarMoney(String num) {
    // Cursor cursor = null;
    // cursor = db.query(DBHelper.CARMONEY_TABLE_NAME, null,
    // DBHelper.carmoney_phone + " =?", new String[]{num.trim()},
    // null, null, null);
    //
    // return getListCarMoney(cursor);
    // }
    //
    // public List<CarInOrOut> getLiCarMoneyNum(String num) {
    // Cursor cursor = null;
    // cursor = db.query(DBHelper.CARMONEY_TABLE_NAME, null,
    // DBHelper.carmoney_number + " =?", new String[]{num.trim()},
    // null, null, null);
    //
    // return getListCarMoney(cursor);
    // }
    //
    // /**
    // * 搜索货车
    // *
    // * @param 车牌号
    // * @return
    // */
    // public CarInOrOut getCarMoney(String number) {
    // CarInOrOut shopcar = null;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CARMONEY_TABLE_NAME, null,
    // DBHelper.carmoney_number + "=?", new String[]{number},
    // null, null, null);
    // Log.d("find", number);
    // if (cursor.moveToFirst()) {
    // shopcar = new CarInOrOut();
    // shopcar.carNum = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmoney_number));
    // shopcar.pnone = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmoney_phone));
    // shopcar.Msg = cursor.getString(cursor
    // .getColumnIndex(DBHelper.carmoney_Msg));
    // }
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return shopcar;
    // }
    //
    // /**
    // * 将所有获取到的卷烟信息保存到数据库中
    // *
    // * @param shopcarList 卷烟列表
    // * @return
    // */
    // public long insertCarMoney(List<CarInOrOut> list) {
    // // deleteLABEL_TABLE_NAME();
    // int count = 0;
    // if (null != list && list.size() > 0) {
    // db.beginTransaction();
    // ContentValues initialValues = null;
    // for (CarInOrOut shopcar : list) {
    // if (hasCarMoneyMsg(shopcar.carNum)) {
    // updateCarMoney(shopcar);
    // } else {
    // initialValues = new ContentValues();
    // initialValues.put(DBHelper.carmoney_number,
    // shopcar.carNum);
    // initialValues.put(DBHelper.carmoney_phone, shopcar.pnone);
    // initialValues.put(DBHelper.carmoney_Msg, shopcar.Msg);
    // db.insert(DBHelper.CARMONEY_TABLE_NAME, null, initialValues);
    // }
    // }
    // // Log.i("tag", shopcar.number + "=添加 car信息成功。. :");
    // // }
    // db.setTransactionSuccessful();
    // db.endTransaction();
    // }
    // Log.i("tag", list.size() + "=总共成度。. :");
    // return count;
    // }
    //
    // public void updateCarMoney(CarInOrOut shopcar) {
    // ContentValues initialValues = getCarMoneyValues(shopcar);
    //
    // int id = db.update(DBHelper.CARMONEY_TABLE_NAME, initialValues,
    // DBHelper.carmoney_number + " =" + "'" + shopcar.carNum + "'",
    // null);
    // // Log.v("tag", shopcar.number + "=更新标签成功。。。");
    // }
    //
    // public Boolean hasCarMoneyMsg(String number) {
    // Boolean b = false;
    // Cursor cursor = null;
    // if (!"".equals(number)) {
    // cursor = db.query(DBHelper.CARMONEY_TABLE_NAME, null,
    // DBHelper.carmoney_number + "=?", new String[]{number},
    // null, null, null);
    // b = cursor.moveToFirst();
    // }
    // if (!cursor.isClosed()) {
    // cursor.close();
    // }
    // return b;
    // }
    //
    // public ContentValues getCarMoneyValues(CarInOrOut shopcar) {
    // ContentValues initialValues = new ContentValues();
    //
    // initialValues.put(DBHelper.carmoney_Msg, shopcar.Msg);
    // initialValues.put(DBHelper.carmoney_number, shopcar.carNum);
    // initialValues.put(DBHelper.carmoney_phone, shopcar.pnone);
    //
    // return initialValues;
    // }
    //
    // public boolean deletecarmoney() {
    // Log.d("zhifubao", "删除欠费记录");
    // return db.delete(DBHelper.CARMONEY_TABLE_NAME, null, null) > 0;
    // }
    //
    // public void deletecarmoneyOne(String carNum) {
    // db.delete(DBHelper.CARMONEY_TABLE_NAME,
    // DBHelper.carmoney_number + "=?", new String[]{carNum});
    // }
}

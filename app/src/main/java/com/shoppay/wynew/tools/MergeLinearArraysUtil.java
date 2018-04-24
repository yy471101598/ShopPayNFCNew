package com.shoppay.wynew.tools;

import java.util.List;

/**
*
* 合并一维数组
* author Yuan Cheng
* blog yuancheng91.top
* email jasoncheng9111@163.com
* created 2017/3/4 21:49
*/
public class MergeLinearArraysUtil
{
    private static byte[] mReturnArray;
    private static int mArrayLength;
    private static byte[] mTempArray;

    public MergeLinearArraysUtil()
    {
        mReturnArray = null;
        mArrayLength = 0;
        mTempArray = null;
    }

    /**
     *
     * @param list 传递一个包含多个一维数组的集合
     * @return 返回合并后的一维数组
     */
    public static byte[] mergeLinearArrays(List<byte[]> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            mArrayLength += list.get(i).length;
        }
        mReturnArray = new byte[mArrayLength];
        mArrayLength = 0;

        for (int j = 0; j < list.size(); j++)
        {
            mTempArray = (byte[])list.get(j);

            if (j == 0)
            {
                System.arraycopy(mTempArray,0,mReturnArray,0,mTempArray.length);
            }
            else
            {
                System.arraycopy(mTempArray,0,mReturnArray,mArrayLength,mTempArray.length);
            }

            mArrayLength += list.get(j).length;
        }

        return mReturnArray;
    }
}

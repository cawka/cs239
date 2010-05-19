// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package FriendDetector;

public final class RecognizerPrxHelper extends Ice.ObjectPrxHelperBase implements RecognizerPrx
{
    public Face[]
    findFaces(byte[] jpegFile, UID userid)
    {
        return findFaces(jpegFile, userid, null, false);
    }

    public Face[]
    findFaces(byte[] jpegFile, UID userid, java.util.Map<String, String> __ctx)
    {
        return findFaces(jpegFile, userid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private Face[]
    findFaces(byte[] jpegFile, UID userid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("findFaces");
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                return __del.findFaces(jpegFile, userid, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public Face[]
    findFacesAndRecognizePeople(byte[] jpegFile, UID userid)
    {
        return findFacesAndRecognizePeople(jpegFile, userid, null, false);
    }

    public Face[]
    findFacesAndRecognizePeople(byte[] jpegFile, UID userid, java.util.Map<String, String> __ctx)
    {
        return findFacesAndRecognizePeople(jpegFile, userid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private Face[]
    findFacesAndRecognizePeople(byte[] jpegFile, UID userid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("findFacesAndRecognizePeople");
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                return __del.findFacesAndRecognizePeople(jpegFile, userid, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public FacePictureWithName
    getTrainSetFace(int num, UID userid)
    {
        return getTrainSetFace(num, userid, null, false);
    }

    public FacePictureWithName
    getTrainSetFace(int num, UID userid, java.util.Map<String, String> __ctx)
    {
        return getTrainSetFace(num, userid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private FacePictureWithName
    getTrainSetFace(int num, UID userid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getTrainSetFace");
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                return __del.getTrainSetFace(num, userid, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getTrainSetSize(UID userid)
    {
        return getTrainSetSize(userid, null, false);
    }

    public int
    getTrainSetSize(UID userid, java.util.Map<String, String> __ctx)
    {
        return getTrainSetSize(userid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTrainSetSize(UID userid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getTrainSetSize");
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                return __del.getTrainSetSize(userid, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    learn(byte[] jpegFileOfFace, String name, UID userid)
    {
        learn(jpegFileOfFace, name, userid, null, false);
    }

    public void
    learn(byte[] jpegFileOfFace, String name, UID userid, java.util.Map<String, String> __ctx)
    {
        learn(jpegFileOfFace, name, userid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    learn(byte[] jpegFileOfFace, String name, UID userid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                __del.learn(jpegFileOfFace, name, userid, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String
    recognizeFace(byte[] jpegFileOfFace, UID userid)
    {
        return recognizeFace(jpegFileOfFace, userid, null, false);
    }

    public String
    recognizeFace(byte[] jpegFileOfFace, UID userid, java.util.Map<String, String> __ctx)
    {
        return recognizeFace(jpegFileOfFace, userid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    recognizeFace(byte[] jpegFileOfFace, UID userid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("recognizeFace");
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                return __del.recognizeFace(jpegFileOfFace, userid, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    unLearn(int id, UID userid)
    {
        unLearn(id, userid, null, false);
    }

    public void
    unLearn(int id, UID userid, java.util.Map<String, String> __ctx)
    {
        unLearn(id, userid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    unLearn(int id, UID userid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                __del.unLearn(id, userid, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public static RecognizerPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        RecognizerPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (RecognizerPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::FriendDetector::Recognizer"))
                {
                    RecognizerPrxHelper __h = new RecognizerPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecognizerPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        RecognizerPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (RecognizerPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::FriendDetector::Recognizer", __ctx))
                {
                    RecognizerPrxHelper __h = new RecognizerPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecognizerPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        RecognizerPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::FriendDetector::Recognizer"))
                {
                    RecognizerPrxHelper __h = new RecognizerPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static RecognizerPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        RecognizerPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::FriendDetector::Recognizer", __ctx))
                {
                    RecognizerPrxHelper __h = new RecognizerPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static RecognizerPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        RecognizerPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (RecognizerPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                RecognizerPrxHelper __h = new RecognizerPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RecognizerPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        RecognizerPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            RecognizerPrxHelper __h = new RecognizerPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _RecognizerDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _RecognizerDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, RecognizerPrx v)
    {
        __os.writeProxy(v);
    }

    public static RecognizerPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            RecognizerPrxHelper result = new RecognizerPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

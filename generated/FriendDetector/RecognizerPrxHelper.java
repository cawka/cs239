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
    findFacesAndRecognizePeople(byte[] jpegFile)
    {
        return findFacesAndRecognizePeople(jpegFile, null, false);
    }

    public Face[]
    findFacesAndRecognizePeople(byte[] jpegFile, java.util.Map<String, String> __ctx)
    {
        return findFacesAndRecognizePeople(jpegFile, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private Face[]
    findFacesAndRecognizePeople(byte[] jpegFile, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                return __del.findFacesAndRecognizePeople(jpegFile, __ctx);
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

    public FacePictureWithName[]
    getTrainSet()
    {
        return getTrainSet(null, false);
    }

    public FacePictureWithName[]
    getTrainSet(java.util.Map<String, String> __ctx)
    {
        return getTrainSet(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private FacePictureWithName[]
    getTrainSet(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTrainSet");
                __delBase = __getDelegate(false);
                _RecognizerDel __del = (_RecognizerDel)__delBase;
                return __del.getTrainSet(__ctx);
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
    learn(byte[] jpegFileOfFace, String name)
    {
        learn(jpegFileOfFace, name, null, false);
    }

    public void
    learn(byte[] jpegFileOfFace, String name, java.util.Map<String, String> __ctx)
    {
        learn(jpegFileOfFace, name, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    learn(byte[] jpegFileOfFace, String name, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __del.learn(jpegFileOfFace, name, __ctx);
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
    recognizeFace(byte[] jpegFileOfFace)
    {
        return recognizeFace(jpegFileOfFace, null, false);
    }

    public String
    recognizeFace(byte[] jpegFileOfFace, java.util.Map<String, String> __ctx)
    {
        return recognizeFace(jpegFileOfFace, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    recognizeFace(byte[] jpegFileOfFace, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                return __del.recognizeFace(jpegFileOfFace, __ctx);
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
    unLearn(int id)
    {
        unLearn(id, null, false);
    }

    public void
    unLearn(int id, java.util.Map<String, String> __ctx)
    {
        unLearn(id, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    unLearn(int id, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __del.unLearn(id, __ctx);
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

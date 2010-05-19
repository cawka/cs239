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

public abstract class _RecognizerDisp extends Ice.ObjectImpl implements Recognizer
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::FriendDetector::Recognizer",
        "::Ice::Object"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[0];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[0];
    }

    public static String
    ice_staticId()
    {
        return __ids[0];
    }

    public final Face[]
    findFaces(byte[] jpegFile, UID userid)
    {
        return findFaces(jpegFile, userid, null);
    }

    public final Face[]
    findFacesAndRecognizePeople(byte[] jpegFile, UID userid)
    {
        return findFacesAndRecognizePeople(jpegFile, userid, null);
    }

    public final FacePictureWithName
    getTrainSetFace(int num, UID userid)
    {
        return getTrainSetFace(num, userid, null);
    }

    public final int
    getTrainSetSize(UID userid)
    {
        return getTrainSetSize(userid, null);
    }

    public final void
    learn(byte[] jpegFileOfFace, String name, UID userid)
    {
        learn(jpegFileOfFace, name, userid, null);
    }

    public final String
    recognizeFace(byte[] jpegFileOfFace, UID userid)
    {
        return recognizeFace(jpegFileOfFace, userid, null);
    }

    public final void
    unLearn(int id, UID userid)
    {
        unLearn(id, userid, null);
    }

    public static Ice.DispatchStatus
    ___findFacesAndRecognizePeople(Recognizer __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] jpegFile;
        jpegFile = FileHelper.read(__is);
        UID userid;
        userid = new UID();
        userid.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        Face[] __ret = __obj.findFacesAndRecognizePeople(jpegFile, userid, __current);
        FacesHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___findFaces(Recognizer __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] jpegFile;
        jpegFile = FileHelper.read(__is);
        UID userid;
        userid = new UID();
        userid.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        Face[] __ret = __obj.findFaces(jpegFile, userid, __current);
        FacesHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___recognizeFace(Recognizer __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] jpegFileOfFace;
        jpegFileOfFace = FileHelper.read(__is);
        UID userid;
        userid = new UID();
        userid.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.recognizeFace(jpegFileOfFace, userid, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___learn(Recognizer __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] jpegFileOfFace;
        jpegFileOfFace = FileHelper.read(__is);
        String name;
        name = __is.readString();
        UID userid;
        userid = new UID();
        userid.__read(__is);
        __is.endReadEncaps();
        __obj.learn(jpegFileOfFace, name, userid, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTrainSetSize(Recognizer __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        UID userid;
        userid = new UID();
        userid.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTrainSetSize(userid, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTrainSetFace(Recognizer __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int num;
        num = __is.readInt();
        UID userid;
        userid = new UID();
        userid.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        FacePictureWithName __ret = __obj.getTrainSetFace(num, userid, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___unLearn(Recognizer __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int id;
        id = __is.readInt();
        UID userid;
        userid = new UID();
        userid.__read(__is);
        __is.endReadEncaps();
        __obj.unLearn(id, userid, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "findFaces",
        "findFacesAndRecognizePeople",
        "getTrainSetFace",
        "getTrainSetSize",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "learn",
        "recognizeFace",
        "unLearn"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___findFaces(this, in, __current);
            }
            case 1:
            {
                return ___findFacesAndRecognizePeople(this, in, __current);
            }
            case 2:
            {
                return ___getTrainSetFace(this, in, __current);
            }
            case 3:
            {
                return ___getTrainSetSize(this, in, __current);
            }
            case 4:
            {
                return ___ice_id(this, in, __current);
            }
            case 5:
            {
                return ___ice_ids(this, in, __current);
            }
            case 6:
            {
                return ___ice_isA(this, in, __current);
            }
            case 7:
            {
                return ___ice_ping(this, in, __current);
            }
            case 8:
            {
                return ___learn(this, in, __current);
            }
            case 9:
            {
                return ___recognizeFace(this, in, __current);
            }
            case 10:
            {
                return ___unLearn(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type FriendDetector::Recognizer was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type FriendDetector::Recognizer was not generated with stream support";
        throw ex;
    }
}

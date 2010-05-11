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

public final class FacePictureWithName implements java.lang.Cloneable, java.io.Serializable
{
    public int id;

    public byte[] jpegFileOfFace;

    public String name;

    public FacePictureWithName()
    {
    }

    public FacePictureWithName(int id, byte[] jpegFileOfFace, String name)
    {
        this.id = id;
        this.jpegFileOfFace = jpegFileOfFace;
        this.name = name;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        FacePictureWithName _r = null;
        try
        {
            _r = (FacePictureWithName)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(id != _r.id)
            {
                return false;
            }
            if(!java.util.Arrays.equals(jpegFileOfFace, _r.jpegFileOfFace))
            {
                return false;
            }
            if(name != _r.name && name != null && !name.equals(_r.name))
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 0;
        __h = 5 * __h + id;
        if(jpegFileOfFace != null)
        {
            for(int __i0 = 0; __i0 < jpegFileOfFace.length; __i0++)
            {
                __h = 5 * __h + (int)jpegFileOfFace[__i0];
            }
        }
        if(name != null)
        {
            __h = 5 * __h + name.hashCode();
        }
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeInt(id);
        FileHelper.write(__os, jpegFileOfFace);
        __os.writeString(name);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        id = __is.readInt();
        jpegFileOfFace = FileHelper.read(__is);
        name = __is.readString();
    }
}

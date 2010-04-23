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

public final class Face implements java.lang.Cloneable, java.io.Serializable
{
    public FacePosition position;

    public String name;

    public Face()
    {
    }

    public Face(FacePosition position, String name)
    {
        this.position = position;
        this.name = name;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        Face _r = null;
        try
        {
            _r = (Face)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(position != _r.position && position != null && !position.equals(_r.position))
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
        if(position != null)
        {
            __h = 5 * __h + position.hashCode();
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
        position.__write(__os);
        __os.writeString(name);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        position = new FacePosition();
        position.__read(__is);
        name = __is.readString();
    }
}

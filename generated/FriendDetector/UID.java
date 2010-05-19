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

public final class UID implements java.lang.Cloneable, java.io.Serializable
{
    public String facebook;

    public UID()
    {
    }

    public UID(String facebook)
    {
        this.facebook = facebook;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        UID _r = null;
        try
        {
            _r = (UID)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(facebook != _r.facebook && facebook != null && !facebook.equals(_r.facebook))
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
        if(facebook != null)
        {
            __h = 5 * __h + facebook.hashCode();
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
        __os.writeString(facebook);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        facebook = __is.readString();
    }
}

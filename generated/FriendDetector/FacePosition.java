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

public final class FacePosition implements java.lang.Cloneable, java.io.Serializable
{
    public int left;

    public int top;

    public int right;

    public int bottom;

    public FacePosition()
    {
    }

    public FacePosition(int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        FacePosition _r = null;
        try
        {
            _r = (FacePosition)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(left != _r.left)
            {
                return false;
            }
            if(top != _r.top)
            {
                return false;
            }
            if(right != _r.right)
            {
                return false;
            }
            if(bottom != _r.bottom)
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
        __h = 5 * __h + left;
        __h = 5 * __h + top;
        __h = 5 * __h + right;
        __h = 5 * __h + bottom;
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
        __os.writeInt(left);
        __os.writeInt(top);
        __os.writeInt(right);
        __os.writeInt(bottom);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        left = __is.readInt();
        top = __is.readInt();
        right = __is.readInt();
        bottom = __is.readInt();
    }
}

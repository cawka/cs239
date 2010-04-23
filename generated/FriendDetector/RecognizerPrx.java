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

public interface RecognizerPrx extends Ice.ObjectPrx
{
    public Face[] findFacesAndRecognizePeople(byte[] jpegFile);
    public Face[] findFacesAndRecognizePeople(byte[] jpegFile, java.util.Map<String, String> __ctx);

    public Face[] recognizePeople(byte[][] listOfJpegFiles);
    public Face[] recognizePeople(byte[][] listOfJpegFiles, java.util.Map<String, String> __ctx);

    public void learn(Face[] listOfFacesToLearn);
    public void learn(Face[] listOfFacesToLearn, java.util.Map<String, String> __ctx);
}

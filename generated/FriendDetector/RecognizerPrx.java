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

    public Face[] findFaces(byte[] jpegFile);
    public Face[] findFaces(byte[] jpegFile, java.util.Map<String, String> __ctx);

    public String recognizeFace(byte[] jpegFileOfFace);
    public String recognizeFace(byte[] jpegFileOfFace, java.util.Map<String, String> __ctx);

    public void learn(byte[] jpegFileOfFace, String name);
    public void learn(byte[] jpegFileOfFace, String name, java.util.Map<String, String> __ctx);

    public int getTrainSetSize();
    public int getTrainSetSize(java.util.Map<String, String> __ctx);

    public FacePictureWithName getTrainSetFace(int num);
    public FacePictureWithName getTrainSetFace(int num, java.util.Map<String, String> __ctx);

    public void unLearn(int id);
    public void unLearn(int id, java.util.Map<String, String> __ctx);
}

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
    public Face[] findFacesAndRecognizePeople(byte[] jpegFile, UID userid);
    public Face[] findFacesAndRecognizePeople(byte[] jpegFile, UID userid, java.util.Map<String, String> __ctx);

    public Face[] findFaces(byte[] jpegFile, UID userid);
    public Face[] findFaces(byte[] jpegFile, UID userid, java.util.Map<String, String> __ctx);

    public String recognizeFace(byte[] jpegFileOfFace, UID userid);
    public String recognizeFace(byte[] jpegFileOfFace, UID userid, java.util.Map<String, String> __ctx);

    public void learn(byte[] jpegFileOfFace, String name, UID userid);
    public void learn(byte[] jpegFileOfFace, String name, UID userid, java.util.Map<String, String> __ctx);

    public int getTrainSetSize(UID userid);
    public int getTrainSetSize(UID userid, java.util.Map<String, String> __ctx);

    public FacePictureWithName getTrainSetFace(int num, UID userid);
    public FacePictureWithName getTrainSetFace(int num, UID userid, java.util.Map<String, String> __ctx);

    public void unLearn(int id, UID userid);
    public void unLearn(int id, UID userid, java.util.Map<String, String> __ctx);
}

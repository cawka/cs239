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

public interface _RecognizerOperations
{
    Face[] findFacesAndRecognizePeople(byte[] jpegFile, Ice.Current __current);

    Face[] findFaces(byte[] jpegFile, Ice.Current __current);

    String recognizeFace(byte[] jpegFileOfFace, Ice.Current __current);

    void learn(byte[] jpegFileOfFace, String name, Ice.Current __current);

    FacePictureWithName[] getTrainSet(Ice.Current __current);

    void unLearn(int id, Ice.Current __current);
}

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

public interface _RecognizerOperationsNC
{
    Face[] findFacesAndRecognizePeople(byte[] jpegFile);

    Face[] findFaces(byte[] jpegFile);

    String recognizeFace(byte[] jpegFileOfFace);

    void learn(byte[] jpegFileOfFace, String name);

    int getTrainSetSize();

    FacePictureWithName getTrainSetFace(int num);

    void unLearn(int id);
}

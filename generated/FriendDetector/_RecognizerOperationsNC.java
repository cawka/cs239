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
    Face[] findFacesAndRecognizePeople(byte[] jpegFile, UID userid);

    Face[] findFaces(byte[] jpegFile, UID userid);

    String recognizeFace(byte[] jpegFileOfFace, UID userid);

    void learn(byte[] jpegFileOfFace, String name, UID userid);

    int getTrainSetSize(UID userid);

    FacePictureWithName getTrainSetFace(int num, UID userid);

    void unLearn(int id, UID userid);
}

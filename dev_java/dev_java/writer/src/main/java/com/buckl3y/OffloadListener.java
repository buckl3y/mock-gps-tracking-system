package com.buckl3y;

import java.util.List;

public interface OffloadListener {
    void batchOffload(List<Message> batch);
}

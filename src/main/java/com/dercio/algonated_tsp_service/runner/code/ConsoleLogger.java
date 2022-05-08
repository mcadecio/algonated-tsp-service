package com.dercio.algonated_tsp_service.runner.code;

import java.util.ArrayList;
import java.util.List;

public class ConsoleLogger {

    private final List<String> lines = new ArrayList<>();

    public void print(String value) {
        lines.add(value);
    }

    public void println(String value) {
        lines.add("\n");
        print(value);
    }

    public List<String> getLines() {
        return lines;
    }
}

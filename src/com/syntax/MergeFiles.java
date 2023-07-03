package com.syntax;

import java.io.*;

public class MergeFiles {
    public static void main(String[] args) {
        String directoryPath = ""; // 디렉터리 경로를 지정해주세요
        String outputFilePath = ""; // 결과 파일의 경로와 이름을 지정해주세요

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

            for (File file : files) {
                if (file.isFile()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }

                    reader.close();
                }
            }

            writer.close();
            System.out.println("파일 병합이 완료되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package obfuscator.obfuscator_kr;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static String nameMainClass;
    static String obfCode;
    static String userStr;

    public static String cipherVernam(String initialWord) {
        StringBuilder sb = new StringBuilder();
        int result;
        sb.append('a');
        for (int i = 0; i < initialWord.length(); i++) {
            result = (int)initialWord.charAt(i) ^ (int)userStr.charAt(i);
            sb.append(result);
            if (i != initialWord.length()-1)
                sb.append('_');
        }
        return sb.toString();
    }

    public static String obfuscation(boolean replaceVar, boolean deleteComments, boolean oneString, boolean tabsAndSpace) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(ObfuscatorController.selectedFile.getPath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder nameFile = new StringBuilder();
        for (int i = 0; i < ObfuscatorController.selectedFile.getName().length(); i++) {
            if (!(ObfuscatorController.selectedFile.getName().charAt(i) == '.')) {
                nameFile.append(ObfuscatorController.selectedFile.getName().charAt(i));
            } else break;
        }
        nameMainClass = nameFile.toString();
        if (deleteComments) {
            Pattern patternComments = Pattern.compile("(/\\*(.|\n|\r)*\\*/)|(//.*)");
            Matcher matcherComments = patternComments.matcher(sb.toString());
            while (matcherComments.find()) {
                int start;
                int end;
                start = matcherComments.start();
                end = matcherComments.end();
                System.out.println(matcherComments.group());
                sb.delete(start, end);
                matcherComments.reset(sb.toString());
            }
        }

        ArrayList<String> strings = new ArrayList<>();
        boolean status = false;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\"') {
                if (sb.charAt(i-1) != '\\') {
                    if (status == false)
                        status = true;
                    else status = false;
                    continue;
                }
            }
            if (status) {
                temp.append(sb.charAt(i));
            }
            else if (status == false && !temp.toString().equals("")) {
                strings.add(temp.toString());
                temp.delete(0, temp.length());
            }
        }
        if (tabsAndSpace) {
            int i = 0;
            int countTabs = 0;
            int countSpaceString = 0;
            int countSpace = 0;
            while (i != sb.length()-1) {
                if ((i != 0) && (sb.charAt(i-1) == '\n')) {
                    countSpace = countTabs * 4;
                    while (sb.charAt(i) == ' ') {
                        countSpaceString++;
                        i++;
                    }
                    if (sb.charAt(i) != ' ') {
                        if (sb.charAt(i) != '}') {
                            if (countSpaceString < countSpace) {
                                while (countSpaceString != countSpace) {
                                    sb.insert(i, ' ');
                                    countSpaceString++;
                                    i++;
                                }
                                if (sb.charAt(i) == '{')
                                    countTabs++;
                                i++;
                                countSpaceString = 0;
                            } else {
                                if (countSpace == countSpaceString) {
                                    countSpaceString = 0;
                                    if (sb.charAt(i) == '{')
                                        countTabs++;
                                    i++;
                                }
                                else if (countSpaceString > countSpace) {
                                    while (countSpaceString != countSpace) {
                                        sb.deleteCharAt(i-1);
                                        countSpaceString--;
                                        i--;
                                    }
                                    if (sb.charAt(i) == '{')
                                        countTabs++;
                                    countSpaceString = 0;
                                    i++;
                                }
                            }
                        } else {
                            if ((countSpaceString < countSpace - 4) && (countSpace != 0)) {
                                while (countSpaceString != countSpace - 4) {
                                    sb.insert(i, ' ');
                                    countSpaceString++;
                                    i++;
                                }
                                i++;
                                countSpaceString = 0;
                                countTabs--;
                            } else {
                                if (countSpace - 4 == countSpaceString) {
                                    countSpaceString = 0;
                                    i++;
                                    countTabs--;
                                }
                                else if ((countSpace - 4 < countSpaceString) && (countSpace >= 4)) {
                                    while (countSpaceString != countSpace - 4) {
                                        sb.deleteCharAt(i - 1);
                                        countSpaceString--;
                                        i--;
                                    }
                                    i++;
                                    countTabs--;
                                    countSpaceString = 0;
                                }
                            }
                        }
                    }
                }
                else if (sb.charAt(i) == '}') {
                    countTabs--;
                    i++;
                    continue;
                }
                else if (sb.charAt(i) == '{')
                {
                    countTabs++;
                    i++;
                    continue;
                }
                else i++;
            }
            i = 0;
            boolean brace = false;
            boolean codeWord = false;
            boolean status1 = true;
            int countIf = 0;
            int count = 0;
            int j;
            ArrayList <Integer> countBraceOpen = new ArrayList<>();
            while (i != sb.length()-1) {
                if (sb.charAt(i) == 'i') {
                    if (sb.charAt(i+1) == 'f') {
                        brace = false;
                        codeWord = true;
                        status1 = true;
                        j = i;
                        while (sb.charAt(j) != '\n') {
                            if (sb.charAt(j) == '{') {
                                status1 = false;
                            }
                            j++;
                        }
                        if (status1) {
                            countIf++;
                        }
                    }
                }
                if (codeWord) {
                    if (sb.charAt(i) == '{') {
                        brace = true;
                        countBraceOpen.add(1);
                    }
                    if (sb.charAt(i) == '}') {
                        countBraceOpen.remove(countBraceOpen.size()-1);
                    }
                    if ((sb.charAt(i) == '\n') && (!brace) && (countBraceOpen.size() != 0)) {
                        sb.insert(i+1, "    ");

                        brace = true;

                    }
                }
                if (countBraceOpen.size() == 0) {
                    brace = false;
                }
                if (codeWord) {
                    if ((brace == true) && (countBraceOpen.size() == 0)) {
                        codeWord = false;
                        continue;
                    }
                    if ((sb.charAt(i) == '\n') && (countBraceOpen.size() == 0) && (sb.charAt(i-2) != '}')) {
                        sb.insert(i+1, "    ");
                        codeWord = false;
                    }
                    if ((sb.charAt(i) == '\n') && (countBraceOpen.size() == 0) && (sb.charAt(i-2) == '}')) {
                        codeWord = false;
                    }
                    if ((sb.charAt(i) == '\n') && (countBraceOpen.size() == 0)) {
                        countIf--;
                    }
                    if ((sb.charAt(i) == '\n') && (countBraceOpen.size() > 0)) {
                        while (count != countIf) {
                            sb.insert(i + 1, "    ");
                            count++;
                        }
                        count = 0;
                    }
                }
                i++;
            }

        }
        if (replaceVar) {
            String textFile = sb.toString();
            ArrayList<String> arrTypes = new ArrayList<>(Arrays.asList("int", "float", "boolean", "void", "String", "double", "byte", "Activity", "AlertDialog", "Bundle", "Button", "Toast"));
            String className = "class";
            String regexClass = String.format("class [\\w]+", className);
            Pattern patternClass = Pattern.compile(regexClass);
            Matcher matcherClass = patternClass.matcher(textFile);
            String tempString;
            String tempVernam;
            Map<String, String> dictionaryVar = new HashMap<String, String>();
            String stringFile;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < ObfuscatorController.selectedFile.getName().length(); i++) {
                if (!(ObfuscatorController.selectedFile.getName().charAt(i) == '.')) {
                    stringBuilder.append(ObfuscatorController.selectedFile.getName().charAt(i));
                } else break;
            }
            stringFile = stringBuilder.toString();
            while (matcherClass.find()) {
                arrTypes.add(matcherClass.group().toString().replaceAll("class ", ""));
                tempString = matcherClass.group().toString().replaceAll("class ", "");
                tempVernam = cipherVernam(tempString);
                dictionaryVar.put(tempString, tempVernam);
            }
            System.out.println(arrTypes.toString());
            for (String attribute : arrTypes) {
                String regex = String.format("%s[ \\\\[\\\\]]+[\\w]+", attribute);
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(textFile);
                System.out.println("---------" + attribute + "---------");
                String tempKey;
                String tempValue;
                while (matcher.find()) {
                    tempKey = matcher.group().toString().replaceAll(String.format("%s[ \\\\[\\\\]]+", attribute), "");
                    System.out.println(tempKey);
                    tempValue = cipherVernam(tempKey);
                    dictionaryVar.put(tempKey, tempValue);
                }
            }
            int startVar;
            int endVar;
            ArrayList<String> startChar = new ArrayList<>();
            String startPoolChars = " .,[]({*-+/%&;:|)}=<>!";
            char[] startCharArray = startPoolChars.toCharArray();
            for (char c : startCharArray) {
                startChar.add(Character.toString(c));
            }
            for (Map.Entry<String, String> pair : dictionaryVar.entrySet()) {
                if (!pair.getKey().toString().equals("main")) {
                    if (stringFile.equals(pair.getKey())) {
                        nameMainClass = pair.getValue();
                    }
                    String regexChange = pair.getKey();
                    Pattern patternChange = Pattern.compile(regexChange);
                    Matcher matcherChange = patternChange.matcher(sb.toString());
                    while (matcherChange.find()) {
                        startVar = matcherChange.start();
                        endVar = matcherChange.end();
                        if (startChar.contains(Character.toString(sb.charAt(startVar - 1))) && startChar.contains(Character.toString(sb.charAt(endVar)))) {
                            sb = sb.replace(startVar, endVar, pair.getValue());
                            matcherChange.reset(sb.toString());
                        }
                    }
                }
            }
        }

        if (oneString) {
            for (int i = 0; i < sb.length(); i++) {
                if ((sb.charAt(i) == '\n') || (sb.charAt(i) == '\r')) {
                    sb.deleteCharAt(i);
                    i--;
                }
            }
        }


        int i = 0;
        int j = 0;
        int lengthString;
        int startIndex = 0;
        int endIndex = 0;
        status = false;
        while (i != sb.length()-1) {
            if (sb.charAt(i) == '\"') {
                if (sb.charAt(i-1) != '\\') {
                    if (status == false) {
                        status = true;
                        startIndex = i+1;
                        i++;
                    }
                    else {
                        status = false;
                        endIndex = i;
                        sb.replace(startIndex, endIndex, strings.get(j));
                        lengthString = strings.get(j).length();
                        j++;
                        i = startIndex + lengthString + 1;
                    }
                } else i++;
            }
            else i++;
        }

        System.out.println(sb);
        obfCode = sb.toString();
        return sb.toString();
    }

    public static String getText() {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(ObfuscatorController.selectedFile.getPath()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
    }
}

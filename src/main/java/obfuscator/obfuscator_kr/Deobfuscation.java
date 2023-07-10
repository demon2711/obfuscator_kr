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

public class Deobfuscation {
    static String nameMainClass;
    static String deobfCode;

    public static String cipherVernamDeobf(String initialWord) {
        StringBuilder word = new StringBuilder();
        StringBuilder newWord = new StringBuilder();
        int temp = 0;
        int charValue = 0;
        int result = 0;
        int countCodeWord = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(initialWord);
        for (int i = 1; i < sb.length(); i++) {
            if (sb.charAt(i) != '_')
                word.append(sb.charAt(i));
            if ((i == sb.length()-1) || (sb.charAt(i) == '_')) {
                for (int j = 0; j < word.length(); j++) {
                    charValue = Character.getNumericValue(word.charAt(j));
                    temp = temp + charValue;
                    if (j != word.length() - 1)
                        temp *= 10;
                }
                result = temp ^ (int) Main.userStr.charAt(countCodeWord);
                newWord.append((char) result);
                temp = 0;
                word.delete(0, word.length());
                countCodeWord++;
            }
        }
        return newWord.toString();
    }
    public static String deobfuscation() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(ObfuscatorController.selectedFile.getPath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String textFile = sb.toString();
        ArrayList<String> arrTypes = new ArrayList<>(Arrays.asList("int", "float", "boolean", "void", "String", "double", "byte", "Activity", "AlertDialog", "Bundle", "Button", "Toast"));
        ArrayList<String> strings = new ArrayList<>();
        Pattern patternStrings = Pattern.compile("(\\\"[^\\\"]*\\\")");
        Matcher matcherStrings = patternStrings.matcher(sb.toString());
        while (matcherStrings.find()) {
            strings.add(matcherStrings.group());
        }
        String className  = "class";
        String regexClass = String.format("class [\\w]+", className);
        Pattern patternClass = Pattern.compile(regexClass);
        Matcher matcherClass = patternClass.matcher(textFile);
        String tempString;
        String tempVernam;
        Map <String, String> dictionaryVar = new HashMap<String, String>();
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
            tempVernam = cipherVernamDeobf(tempString);
            dictionaryVar.put(tempString, tempVernam);
        }
        for (String attribute: arrTypes) {
            String regex = String.format("%s[ \\\\[\\\\]]+[\\w]+", attribute);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(textFile);
            String tempKey;
            String tempValue;
            while (matcher.find()) {
                tempKey = matcher.group().toString().replaceAll(String.format("%s[ \\\\[\\\\]]+", attribute), "");
                System.out.println(tempKey);
                tempValue = cipherVernamDeobf(tempKey);
                dictionaryVar.put(tempKey, tempValue);
            }
        }
        int startVar;
        int endVar;
        System.out.println(dictionaryVar);
        ArrayList<String> startChar = new ArrayList<>();
        String startPoolChars = " .,[]({*-+/%&;:|)}=<>!";
        char [] startCharArray = startPoolChars.toCharArray();
        for (char c: startCharArray) {
            startChar.add(Character.toString(c));
        }
        System.out.println(startChar);
        for (Map.Entry<String, String> pair: dictionaryVar.entrySet()) {

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
        StringBuilder newSb = new StringBuilder();
        int startAdd = 0;
        for (String attribute: strings) {
            int start;
            int end;
            Pattern patternStringsReturn = Pattern.compile("(\\\"[^\\\"]*\\\")");
            Matcher matcherStringsReturn = patternStrings.matcher(sb.toString());
            matcherStringsReturn.find();
            start = matcherStringsReturn.start();
            end = matcherStringsReturn.end();
            sb.replace(start, end, attribute);
            end = start + attribute.length();
            newSb.append(sb.substring(startAdd, end));
            sb.delete(startAdd, end);
        }
        newSb.append(sb);
        deobfCode = newSb.toString();
        return newSb.toString();

    }


}


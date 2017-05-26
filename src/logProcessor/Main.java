package logProcessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    final static Pattern pattern = Pattern.compile("(?:DBCohesionException: \\w*\\: )(.*?)(?= table.)");

    public static void main(String[] args) {
        String pathToLogsDir;
        if (args.length > 0) {
            try {
                pathToLogsDir = args[0];
                List<String> summary = new LinkedList<>();
                List<File> subDirs = getSubs(pathToLogsDir);
                for(File subDir : subDirs){
                    List<String> occurrencesForMonth = new LinkedList<>();
                    List<File> logs = getSubs(subDir.getAbsolutePath());
                    for(File log : logs){
                        try (Stream<String> stream = Files.lines(Paths.get(log.getAbsolutePath()))){
                            List<String>occurencesForDay = stream
                                    .filter(pattern.asPredicate())
                                    .map(Main::returnMatch)
                                    .collect(Collectors.toList());
                            occurrencesForMonth.addAll(occurencesForDay);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(subDir.getName()+":");
                    Set<String> uniqueSet = new HashSet<>(occurrencesForMonth);
                    for (String temp : uniqueSet) {
                        System.out.println(temp + " x" + Collections.frequency(occurrencesForMonth, temp));
                    }
                    System.out.println("\n\n\n");
                    summary.addAll(occurrencesForMonth);
                }
                System.out.println("Сумма:");
                Set<String> uniqueSet = new HashSet<>(summary);
                for (String temp : uniqueSet) {
                    System.out.println(temp + " x" + Collections.frequency(summary, temp));
                }
            } catch (Exception e) {
                System.err.println("Argument" + args[0] + " is not a valid directory. \n Or java doesn't think so");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.err.println("Argument is not present.");
            System.exit(1);
        }
    }

    public static List<File> getSubs(String directoryName) {
        File logsDir = new File(directoryName);

        List<File> resultList = new ArrayList<>();

        // get all the files from a logsDir
        File[] subDirList = logsDir.listFiles(file -> !Objects.equals(file.getName().substring(0, 1), "."));
        resultList.addAll(Arrays.asList(subDirList));
        return resultList;
    }


    static String returnMatch(String x){
        Matcher matcher = pattern.matcher(x);
        if (matcher.find()){
            return matcher.group(1);
        } else {
            return x;
        }
    }
}



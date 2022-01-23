import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHandling {
    public static void main(String[] args) {

        Path unsortedDirectory = Paths.get("D:\\unsorted");
        Path sortedDirectory = Paths.get("D:\\sorted");

        try {
            fileOrganizer(unsortedDirectory, sortedDirectory);
            summaryCreator(sortedDirectory);

        } catch(IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * This Method gets files in any directory and returns a list of files in that directory
     *
     * @param path it's a given path to extract files in that path
     * @return the list of files in the given path
     */
    public static List<File> getFiles(Path path) {
        List<File> results = new ArrayList<>();
        try(Stream<Path> subPaths = Files.walk(path, Integer.MAX_VALUE)) {
            results = subPaths.filter(e -> e.toFile().isFile() || e.toFile().isHidden()).map(Path :: toFile).collect(Collectors.toList());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    /**
     * This Method get,move and organize the files from one directory to another.
     * The way of organizing is base by extension of each file
     *
     * @param source      The source directory which we want to get our files.
     * @param destination The destionation directory which we want to move and organize our file.
     * @throws IOException this method throws an exception because of use of fileMover method
     */
    public static void fileOrganizer(Path source, Path destination) throws IOException {

        List<File> files = getFiles(source);

        for(File file : files) {
            String extension = getExtension(file.getName());
            directoryCreator(destination, extension);
            Path fileSource = file.toPath();
            Path fileDestination = destination.resolve(extension).resolve(file.getName());
            if(file.isHidden()) {
                extension = "hidden";
                directoryCreator(destination, extension);
                fileDestination = destination.resolve(extension).resolve(file.getName());
            }
            fileMover(fileSource, fileDestination);
        }
    }

    /**
     * This Method make a folder and text file of summary information in that path.
     * like all the file and extensions and if they are hidden or readable or writeable.
     *
     * @param path any given path to analyze and extract the information.
     * @throws IOException throws an exception because of use of built-in methods.
     */
    public static void summaryCreator(Path path) throws IOException {
        List<File> availableFiles = getFiles(path);
        Set<String> availableExtension = extensionsFinder(availableFiles);
        List<String> summaryTexts = new ArrayList<>();

        String sum = "/summary/summary.txt";
        Path summaryPath = Paths.get(path + sum);
        directoryCreator(path, "summary");
        summaryTexts.add(String.format("%-50s%13s%13s%13s", "name", "readable", "writeable", "hidden"));

        for(String extension : availableExtension) {
            summaryTexts.add("\n");
            summaryTexts.add(extension + ":");
            summaryTexts.add("----------");
            for(File file : availableFiles) {
                if(extension.equals(file.getParentFile().getName())) {
                    summaryTexts.add(String.format("%-50s%13s%13s%13s", file.getName(), file.canRead() ? "Y" : "N", file.canWrite() ? "Y" : "N", file.isHidden() ? "Y" : "N"));
                }
            }
        }

        if(!summaryPath.toFile().exists()) {
            Files.createFile(summaryPath);
        }
        Files.write(summaryPath, summaryTexts);
    }


    /**
     * This method is get extension of any given file and returns a string of that extension
     *
     * @param file it's String of a file
     * @return returns extension of file as string
     */
    private static String getExtension(String file) {
        String ext = null;
        try {
            if(file != null) {
                ext = file.substring(file.lastIndexOf('.') + 1);
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
        return ext;
    }

    /**
     * This method make a list of extensions from any given list of files.
     * we give it a list, and it gives us a list of extensions of that list.
     *
     * @param files list of files to extract the extension
     * @return set of string of extensions
     */

    public static Set<String> extensionsFinder(List<File> files) {
        HashSet<String> extensions = new HashSet<>();
        for(File file : files) {
            int i = file.toString().lastIndexOf('.');
            if(i >= 0) {
                extensions.add(file.toString().substring(i + 1));
                if(file.isHidden()) {
                    extensions.add("hidden");
                }
            }
        }
        return extensions;
    }

    /**
     * This Method creates a folder in any given directory
     *
     * @param destination any given destination path which we want to have our folder in .
     * @param folderName  folder name.
     */
    public static void directoryCreator(Path destination, String folderName) {
        File directory = new File(String.valueOf(destination.resolve(folderName)));
        if(!directory.exists()) {
            directory.mkdir();
        }

    }

    /**
     * Moving file from one directory to another
     *
     * @param source      the source of files which we want to  move
     * @param destination the destination folder.
     * @throws IOException this method throws an IOException because of use of move method in files.
     */

    public static void fileMover(Path source, Path destination) throws IOException {
        Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }


}

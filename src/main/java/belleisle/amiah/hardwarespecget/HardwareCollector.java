package belleisle.amiah.hardwarespecget;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.ArrayList;
import java.util.Optional;

public abstract class HardwareCollector {

    private static SystemInfo sysInfo = new SystemInfo();
    private static HardwareAbstractionLayer hal = sysInfo.getHardware();

    public static String getCPU(){
        return beatifyName(hal.getProcessor().getProcessorIdentifier().getName());
    }

    public static ArrayList<String> getGPUs(){
            ArrayList<String> graphicsCards = new ArrayList<>();
            for (GraphicsCard g : hal.getGraphicsCards()) {
                graphicsCards.add(beatifyName(g.getName()));
            }
            return graphicsCards;
    }

    public static String getOS(){
        return sysInfo.getOperatingSystem().getFamily();
    }

    public static String getKernel() {
        // Ignore getting kernel on windows since it will just return a similar value as getOS()
        // For example on Windows 10, the result will be "Windows 10 10.0"
        if (sysInfo.getOperatingSystem().getFamily().equalsIgnoreCase("windows")) {
            return "Windows Kernel";
        }
        return System.getProperty("os.name") + " " + System.getProperty("os.version");
    }

    /**
     * Gets the name and version of the motherboard. Will exclude
     * name and/or version if they are unknown. If both are unknown an
     * empty string will be returned.
     *
     * @return The name and version of the motherboard
     */
    public static String getMotherboard() {
        String motherboard = hal.getComputerSystem().getManufacturer();
        String version = hal.getComputerSystem().getModel();
        StringBuilder sb = new StringBuilder();

        if (!motherboard.equalsIgnoreCase("unknown")) {
            sb.append(motherboard);
            sb.append(" ");
        }
        if (!version.equalsIgnoreCase("unknown")) {
            sb.append(version);
        }

        return sb.toString();
    }

    public static String getUsername() {
        return toTitle(System.getProperty("user.name"));
    }

    /**
     * Removes unnecessary text from a string.
     *
     * @param name The text to beautify
     * @return Beautified text
     */
    public static String beatifyName(String name) {
        name = deepExtractFromBrackets(name);
        name = removeRedundantText(name);
        return name;
    }

    /**
     * Removes redundant text that makes the string harder to read.
     *
     * @param name Text to remove text from
     * @return name with redundant text removed
     */
    private static String removeRedundantText(String name) {
        int searchIndex = 0;
        while (searchIndex != -1) {
            searchIndex = name.indexOf('(', searchIndex);

            if (name.startsWith("(R)", searchIndex)) {
                name = name.substring(0, searchIndex) + name.substring(searchIndex + 3);
                searchIndex++;
            }
            else if (name.startsWith("(TM)", searchIndex)) {
                name = name.substring(0, searchIndex) + name.substring(searchIndex + 4);
                searchIndex++;
            }
        }

        return name;
    }

    /**
     * Extracts the text from the innermost set of square brackets.
     *
     * @param text Text to remove brackets from
     * @return name without brackets
     */
    private static String deepExtractFromBrackets(String text) {
        int beginIndex = 0;
        int endIndex = text.length();

        beginIndex = text.indexOf('[', beginIndex);
        endIndex = text.lastIndexOf(']', endIndex);

        // Check for valid indices and that the brackets are properly closed
        if ((beginIndex != -1 && endIndex != -1) && beginIndex < endIndex) {
            // Get the text inside the found brackets, then look for more
            text = text.substring(beginIndex + 1, endIndex);
            return deepExtractFromBrackets(text);
        }
        else {
            return text;
        }
    }

    private static String toTitle(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

}

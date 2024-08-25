package belleisle.amiah.hardwarespecget;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.ArrayList;
import java.util.Optional;

public abstract class HardwareCollector {

    private static SystemInfo sysInfo = new SystemInfo();
    private static HardwareAbstractionLayer hal = sysInfo.getHardware();

    /**
     * @return The CPU name and clock speed
     */
    public static String getCPU(){
        return beatifyName(hal.getProcessor().getProcessorIdentifier().getName());
    }

    /**
     * Gets all detected GPUs. This includes integrated graphics
     * and dedicated graphics cards.
     *
     * @return A list of detected GPUs with formatted names
     */
    public static ArrayList<String> getGPUs(){
            ArrayList<String> graphicsCards = new ArrayList<>();
            for (GraphicsCard g : hal.getGraphicsCards()) {
                graphicsCards.add(beatifyName(g.getName()));
            }
            return graphicsCards;
    }

    /**
     * Gets the used and total RAM
     *
     * @return RAM used out of total RAM
     */
    public static String getRAM() {
        double usedMem = (double) hal.getMemory().getAvailable() / 1000000000;
        double totalMem = (double) hal.getMemory().getTotal() / 1000000000;

        return String.format("%.2f", usedMem) + " / " + String.format("%.2f", totalMem) + " GB";
    }

    /**
     * Gets the name of disks on the system;
     *
     * @return The name of the disk
     */
    public static ArrayList<String> getDisk() {

        ArrayList<String> disks = new ArrayList<>();
        for (HWDiskStore ds : hal.getDiskStores()) {
            disks.add(beatifyName(ds.getModel()));
        }
        return disks;
    }

    /**
     * Gets the operating system family.
     * <p>
     * On Linux, this will be Debian, Ubuntu, Fedora, etc.
     * </p>
     * <p>
     * On Windows, this will be Windows 10, Windows 11, etc.
     * </p>
     *
     * @return The operating system family
     */
    public static String getOS(){
        if (sysInfo.getOperatingSystem().getFamily().equalsIgnoreCase("windows")) {
            return System.getProperty("os.name");
        }
        return sysInfo.getOperatingSystem().getFamily();
    }

    /**
     * On Linux systems, returns the kernel version.
     * <p>
     * On Windows systems, returns "Windows Kernel" regardless of Windows version.
     * This method is considered redundant on Windows systems. Use getOS() instead.
     * </p>
     * @return The kernel name and version
     */
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

    /**
     * Gets the name of the system's current user.
     * The result is formatted to be lowercase with the first
     * letters of words capitalized.
     *
     * @return The name of the system's current user
     */
    public static String getUsername() {
        return toTitle(System.getProperty("user.name")).trim();
    }

    /**
     * Creates NodeInfo objects for the CPU, GPUs, RAM, Disk, and Motherboard,
     * and places them in a list.
     *
     * @return A list of NodeInfo objects with all hardware types
     * @see NodeInfo
     */
    public static ArrayList<NodeInfo> collectHardware() {
        // Store collected hardware in this list
        ArrayList<NodeInfo> nodeList = new ArrayList<>();

        // Add each type of hardware to the list
        nodeList.add(new NodeInfo(NodeInfo.HardwareType.CPU));
        // Storing size of list, since getGPUs() doesn't need to be called each iteration
        int numGPUs = getGPUs().size();
        for (int i = 0; i < numGPUs; i++) {
            // Adding a GPU entry for every GPU found
            nodeList.add(new NodeInfo(NodeInfo.HardwareType.GPU, i));
        }
        nodeList.add(new NodeInfo(NodeInfo.HardwareType.RAM));

        int numDisks = getDisk().size();
        for (int i = 0; i < numDisks; i++) {
            // Adding a Disk entry for every disk found
            nodeList.add(new NodeInfo(NodeInfo.HardwareType.DISK, i));
        }
        nodeList.add(new NodeInfo(NodeInfo.HardwareType.MOTHERBOARD));

        return nodeList;
    }

    /**
     * Creates NodeInfo objects for the OS, Kernel, and Username,
     * and places them in a list.
     * <p>
     * If the OS is Windows, the list will exclude the kernel.
     * </p>
     *
     * @return A list of NodeInfo objects with system properties
     * @see NodeInfo
     */
    public static ArrayList<NodeInfo> collectSystemInfo() {
        // Store collected system info in this list
        ArrayList<NodeInfo> nodeList = new ArrayList<>();

        nodeList.add(new NodeInfo(NodeInfo.HardwareType.OS));

        // Do not add kernel to list on windows
        if (!sysInfo.getOperatingSystem().getFamily().equalsIgnoreCase("windows")) {
            nodeList.add(new NodeInfo(NodeInfo.HardwareType.KERNEL));
        }

        nodeList.add(new NodeInfo(NodeInfo.HardwareType.USERNAME));

        return nodeList;
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
            // Update the search index
            searchIndex++;
            searchIndex = name.indexOf('(', searchIndex);

            if (name.startsWith("(R)", searchIndex)) {
                name = name.substring(0, searchIndex) + name.substring(searchIndex + 3);
            }
            else if (name.startsWith("(TM)", searchIndex)) {
                name = name.substring(0, searchIndex) + name.substring(searchIndex + 4);
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

    /**
     * Capitalizes each letter before a space (and at the beginning of the string),
     * and makes all other letters lowercase.
     *
     * @param text The text to turn into a title
     * @return The text in title format
     */
    private static String toTitle(String text) {
        int searchIndex = 0;
        // Capitalize the first letter, lowercase all other letters
        text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();

        // Then if spaces are detected capitalize each letter after a space
        while (searchIndex != -1) {
            // Update the search index
            searchIndex++;
            searchIndex = text.indexOf(' ', searchIndex);

            // Ensure indices are valid
            if ((searchIndex > -1) && (searchIndex + 2 <= text.length())) {
                text = text.substring(0, searchIndex + 1)
                        + text.substring(searchIndex + 1, searchIndex + 2).toUpperCase()
                        + text.substring(searchIndex + 2);
            }
        }
        return text;
    }

}

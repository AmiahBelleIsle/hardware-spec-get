package belleisle.amiah.hardwarespecget;

import javafx.beans.property.SimpleBooleanProperty;

public class NodeInfo {

    private HardwareType type;
    private int index = 0;
    private SimpleBooleanProperty isShown = new SimpleBooleanProperty(true);
    private String userTitle;
    private String userContent;

    private String mainColor = "#5f6264";

    public NodeInfo(HardwareType type) {
        this.type = type;
    }

    public NodeInfo(HardwareType type, int index) {
        this.type = type;
        this.index = index;
    }

    public NodeInfo(HardwareType type, int index, boolean isShown) {
        this.type = type;
        this.index = index;
        this.isShown.set(isShown);
    }

    public String getTitle() {
        return switch (type) {
            case CPU -> "CPU";
            case GPU -> "GPU";
            case DISK -> "Disk";
            case RAM -> "RAM";
            case MOTHERBOARD -> "Motherboard";
            case DISPLAY -> "Display";
            case OS -> "Operating System";
            case KERNEL -> "Kernel";
            case USERNAME -> "Username";
            case USERDATA -> userTitle;
        };
    }

    public String getContent() {
        return switch (type) {
            case CPU -> HardwareCollector.getCPU();
            case GPU -> HardwareCollector.getGPUs().get(index);
            case DISK -> "DISK CONTENT";
            case RAM -> "RAM CONTENT";
            case MOTHERBOARD -> HardwareCollector.getMotherboard();
            case DISPLAY -> "Not yet implemented";
            case OS -> HardwareCollector.getOS();
            case KERNEL -> HardwareCollector.getKernel();
            case USERNAME -> HardwareCollector.getUsername();
            case USERDATA -> userContent;
        };
    }

    // Getters
    public boolean getIsShown() {
        return isShown.get();
    }

    protected SimpleBooleanProperty getIsShownProperty() {
        return isShown;
    }

    public int getIndex() {
        return index;
    }

    public HardwareType getType() {
        return type;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public String getUserContent() {
        return userContent;
    }

    public String getMainColor() {
        return mainColor;
    }

    // Setters
    public void setIsShown(boolean show) {
        isShown.set(show);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setType(HardwareType type) {
        this.type = type;
    }

    public void setMainColor(String color) {
        mainColor = color;
    }

    public void setUserTitle(String title) {
        userTitle = title;
    }

    public void setUserContent(String content) {
        userContent = content;
    }

    // Enums
    public enum HardwareType {
        CPU("CPU"),
        GPU("GPU"),
        DISK("DISK"),
        RAM("RAM"),
        MOTHERBOARD("MOTHERBOARD"),
        DISPLAY("DISPLAY"),
        OS("OS"),
        KERNEL("KERNEL"),
        USERNAME("USERNAME"),
        USERDATA("USERDATA");

        private final String valueAsString;

        private HardwareType(String valueAsString) {
            this.valueAsString = valueAsString;
        }

        public String getValueAsString() {
            return valueAsString;
        }

        public static HardwareType stringToValue(String type) {
            return switch (type.toUpperCase()) {
                case "CPU" -> CPU;
                case "GPU" -> GPU;
                case "DISK" -> DISK;
                case "RAM" -> RAM;
                case "MOTHERBOARD" -> MOTHERBOARD;
                case "DISPLAY" -> DISPLAY;
                case "OS", "OPERATING SYSTEM" -> OS;
                case "KERNEL" -> KERNEL;
                case "USERNAME" -> USERNAME;
                case "USERDATA" -> USERDATA;
              default -> null;
            };
        }

    }

}

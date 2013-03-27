package dk.nsi.stamdata.replication.exceptions;

public class UnsupportedColumnType extends RuntimeException {
    public UnsupportedColumnType(int type) {
        super("Type " + type + " mapping not supported in dynamic view");
    }
}

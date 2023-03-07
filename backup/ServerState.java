package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.Operation;

import java.util.ArrayList;
import java.util.List;

public class ServerState {

    // defenitions
    private final int ACTIVE = 0;
    private final int INACTIVE = 1;

    private List<Operation> ledger;
    private int state;

    public ServerState() {
        this.ledger = new ArrayList<>();
        state = ACTIVE;
    }

    public int getState() {
        return state;
    }

    public void activate() {
        System.out.println("admin activated server");
        state = ACTIVE;
    }

    public void deactivate() {
        System.out.println("admin deactivated server");
        state = INACTIVE;
    }

    public List<Operation> getLedgerState() {
        return ledger;
    }
}

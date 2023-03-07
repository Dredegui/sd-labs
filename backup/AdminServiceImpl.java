package pt.tecnico.distledger.server;

import java.util.Iterator;
import java.util.List;

import javax.swing.text.AbstractDocument.LeafElement;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.ulisboa.tecnico.distledger.contract.admin.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private ServerState state;

    public AdminServiceImpl(ServerState state) {
        this.state = state;
    }

    @Override
    public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        state.activate();
        ActivateResponse response = ActivateResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        state.deactivate();
        DeactivateResponse response = DeactivateResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
        List<Operation> ledger = state.getLedgerState();

        LedgerState.Builder lsBuilder = LedgerState.newBuilder();

        ledger.forEach(op -> {
            DistLedgerCommonDefinitions.Operation.Builder operBuilder;
            operBuilder = DistLedgerCommonDefinitions.Operation.newBuilder();
            OperationType type;
            String name = op.getClass().getSimpleName();
            if (name == "CreateOp") {
                type = OperationType.OP_CREATE_ACCOUNT;
            } else if (name == "DeleteOp") {
                type = OperationType.OP_DELETE_ACCOUNT;
            } else if (name == "TransferOp") {
                type = OperationType.OP_TRANSFER_TO;
                TransferOp top = (TransferOp) op;
                operBuilder.setAmount(top.getAmount());
                operBuilder.setDestUserId(top.getDestAccount());
            } else {
                type = OperationType.OP_UNSPECIFIED;
            }
            operBuilder.setType(type);
            operBuilder.setUserId(op.getAccount());
            DistLedgerCommonDefinitions.Operation operation = operBuilder.build();
            lsBuilder.addLedger(operation);
        });
        LedgerState lstate = lsBuilder.build();
        getLedgerStateResponse response = getLedgerStateResponse.newBuilder().setLedgerState(lstate).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}

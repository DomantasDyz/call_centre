package lt.callcentre.service;

import lt.callcentre.model.Call;
import lt.callcentre.model.CallStatus;
import lt.callcentre.repository.CallRepository;

import java.util.ArrayList;
import java.util.List;

public class CallQueueService {
    private final CallRepository repository;

    public CallQueueService(CallRepository repository) {
        this.repository = repository;
    }

    public void addCall(Call call) {
        repository.save(call);
    }

    public List<Call> getCalls(String operatorId) {
        return repository.getCallsForOperator(operatorId);
    }

    public Call takeNextCall(String operatorId) {
        List<Call> operatorCalls = repository.getCallsForOperator(operatorId);

        for (Call call : operatorCalls) {
            if (call.getStatus() == CallStatus.ACTIVE) {
                throw new IllegalStateException("Šis operatorius jau turi aktyvų skambutį!");
            }
        }

        List<Call> waitingCalls = new ArrayList<>();
        for (Call call : operatorCalls) {
            if (call.getStatus() == CallStatus.WAITING) {
                waitingCalls.add(call);
            }
        }

        if (waitingCalls.isEmpty()) {
            throw new IllegalStateException("Nėra nepakeltų skambučių!");
        }

        Call selected = null;
        for (Call call : waitingCalls) {
            if (selected == null) {
                selected = call;
                continue;
            }

            if (call.isVip() && !selected.isVip()) {
                selected = call;
            } else if (call.isVip() == selected.isVip()
                    && call.getCreatedAt().isBefore(selected.getCreatedAt())) {
                selected = call;
            }
        }

        selected.setStatus(CallStatus.ACTIVE);
        repository.update(selected);
        return selected;
    }

    public Call endActiveCall(String operatorId) {
        List<Call> operatorCalls = repository.getCallsForOperator(operatorId);

        for (Call call : operatorCalls) {
            if (call.getStatus() == CallStatus.ACTIVE) {
                call.setStatus(CallStatus.COMPLETED);
                repository.update(call);
                return call;
            }
        }

        throw new IllegalStateException("Šiuo metu nėra jokių aktyvių skambučių!");
    }

    public void setVip(String operatorId, int callId, boolean vip) {
        Call call = repository.findByIdForOperator(operatorId, callId);

        if (call == null) {
            throw new IllegalArgumentException("Skambučio tokiu ID šiam operatoriui priskirta nėra!");
        }

        if ((call.isVip() && vip) || (!call.isVip() && !vip)) {
            System.err.println("Šis skambutis jau yra " + (vip ? "aukšto prioriteto" : "žemo prioriteto") + "!");
            return;
        }

        call.setVip(vip);
        repository.update(call);
    }
}

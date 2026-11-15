package lt.callcentre.repository;

import lt.callcentre.model.Call;
import java.util.ArrayList;
import java.util.List;

public class InMemoryCallRepository implements CallRepository {
    private final List<Call> calls = new ArrayList<>();

    @Override
    public void save(Call call) {
        calls.add(call);
    }

    @Override
    public List<Call> getCallsForOperator(String operatorId) {
        List<Call> result = new ArrayList<>();
        for (Call call : calls) {
            if (call.getOperatorId().equals(operatorId)) {
                result.add(call);
            }
        }
        return result;
    }

    @Override
    public Call findByIdForOperator(String operatorId, int callId) {
        for (Call call : calls) {
            if (call.getOperatorId().equals(operatorId) && call.getId() == callId) {
                return call;
            }
        }
        return null;
    }

    @Override
    public void update(Call call) {}
}

package lt.callcentre.repository;

import lt.callcentre.model.Call;
import java.util.List;

public interface CallRepository {
    void save(Call call);
    List<Call> getCallsForOperator(String operatorId);
    Call findByIdForOperator(String operatorId, int callId);
    void update(Call call);
}

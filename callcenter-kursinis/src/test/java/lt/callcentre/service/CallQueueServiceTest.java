package lt.callcentre.service;

import lt.callcentre.model.Call;
import lt.callcentre.model.CallStatus;
import lt.callcentre.repository.InMemoryCallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CallQueueServiceTest {
    private CallQueueService service;

    @BeforeEach
    void setUp() {
        InMemoryCallRepository repository = new InMemoryCallRepository();
        service = new CallQueueService(repository);
    }

    @Test
    void oldestRegularCallIsSelected() {
        Call oldCall = new Call(1, "Domantas", "+37060000001", false, LocalDateTime.now().minusMinutes(10));
        Call newCall = new Call(2, "Domantas", "+37060000002", false, LocalDateTime.now().minusMinutes(2));
        service.addCall(oldCall);
        service.addCall(newCall);
        Call selected = service.takeNextCall("Domantas");
        assertEquals(1, selected.getId());
        assertEquals(CallStatus.ACTIVE, selected.getStatus());
    }

    @Test
    void vipCallHasPriority() {
        Call oldRegularCall = new Call(1, "Domantas", "+37060000001", false, LocalDateTime.now().minusMinutes(20));
        Call newVipCall = new Call(2, "Domantas", "+37060000002", true, LocalDateTime.now().minusMinutes(1));
        service.addCall(oldRegularCall);
        service.addCall(newVipCall);
        Call selected = service.takeNextCall("Domantas");
        assertEquals(2, selected.getId());
        assertTrue(selected.isVip());
    }

    @Test
    void emptyQueueReturnsError() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.takeNextCall("Domantas")
        );
        assertEquals("Nėra nepakeltų skambučių!", exception.getMessage());
    }

    @Test
    void operatorCannotReadOrChangeOtherOperatorData() {
        Call operatorACall = new Call(1, "Domantas", "+37060000001", false, LocalDateTime.now());
        Call operatorBCall = new Call(2, "Ernest", "+37061111111", false, LocalDateTime.now());
        service.addCall(operatorACall);
        service.addCall(operatorBCall);

        List<Call> operatorACalls = service.getCalls("Domantas");
        assertEquals(1, operatorACalls.size());
        assertEquals("Domantas", operatorACalls.get(0).getOperatorId());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.setVip("Domantas", 2, true)
        );
        assertFalse(operatorBCall.isVip());
    }

    @Test
    void cannotTakeSecondCallWhileAnotherIsActive() {
        service.addCall(new Call(1, "Domantas", "+37060000001", false, LocalDateTime.now().minusMinutes(5)));
        service.addCall(new Call(2, "Domantas", "+37060000002", false, LocalDateTime.now().minusMinutes(2)));
        service.takeNextCall("Domantas");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.takeNextCall("Domantas")
        );
        assertEquals("Šis operatorius jau turi aktyvų skambutį!", exception.getMessage());
    }
}

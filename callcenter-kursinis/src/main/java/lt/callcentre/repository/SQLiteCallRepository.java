package lt.callcentre.repository;

import lt.callcentre.model.Call;
import lt.callcentre.model.CallStatus;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLiteCallRepository implements CallRepository {
    private final String databaseUrl;

    public SQLiteCallRepository(String databaseFile) {
        File file = new File(databaseFile);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        this.databaseUrl = "jdbc:sqlite:" + databaseFile;
        createTable();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS calls (
                    id INTEGER NOT NULL,
                    operator_id TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    vip INTEGER NOT NULL,
                    created_at TEXT NOT NULL,
                    status TEXT NOT NULL,
                    PRIMARY KEY (operator_id, id)
                )
                """;

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko sukurti duomenų bazės lentelės.", e);
        }
    }

    @Override
    public void save(Call call) {
        String sql = """
                INSERT INTO calls (id, operator_id, phone_number, vip, created_at, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, call.getId());
            statement.setString(2, call.getOperatorId());
            statement.setString(3, call.getPhoneNumber());
            statement.setInt(4, call.isVip() ? 1 : 0);
            statement.setString(5, call.getCreatedAt().toString());
            statement.setString(6, call.getStatus().name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko išsaugoti skambučio.", e);
        }
    }

    @Override
    public List<Call> getCallsForOperator(String operatorId) {
        List<Call> calls = new ArrayList<>();
        String sql = "SELECT * FROM calls WHERE operator_id = ? ORDER BY id";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, operatorId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    calls.add(readCall(result));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko nuskaityti skambučių.", e);
        }

        return calls;
    }

    @Override
    public Call findByIdForOperator(String operatorId, int callId) {
        String sql = "SELECT * FROM calls WHERE operator_id = ? AND id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, operatorId);
            statement.setInt(2, callId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return readCall(result);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko rasti skambučio.", e);
        }

        return null;
    }

    @Override
    public void update(Call call) {
        String sql = """
                UPDATE calls
                SET phone_number = ?, vip = ?, created_at = ?, status = ?
                WHERE operator_id = ? AND id = ?
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, call.getPhoneNumber());
            statement.setInt(2, call.isVip() ? 1 : 0);
            statement.setString(3, call.getCreatedAt().toString());
            statement.setString(4, call.getStatus().name());
            statement.setString(5, call.getOperatorId());
            statement.setInt(6, call.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko atnaujinti skambučio.", e);
        }
    }

    private Call readCall(ResultSet result) throws SQLException {
        Call call = new Call(
                result.getInt("id"),
                result.getString("operator_id"),
                result.getString("phone_number"),
                result.getInt("vip") == 1,
                LocalDateTime.parse(result.getString("created_at"))
        );

        call.setStatus(CallStatus.valueOf(result.getString("status")));
        return call;
    }
}

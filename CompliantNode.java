import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CompliantNode implements Node {

    static int TotalRound = 2;
    boolean[] followees;
    int[] TransactionCompletedByFollowee;
    Set<Transaction> TransactionLeft;
    Transaction myTransaction;
    int round;

    public CompliantNode(double graphEntryFromCLI, double maliciousEntryFromCLI, double initialtransactionFromCLI, int NumberOfRoundFromCLI) {
        Set<Transaction> newTransaction = new HashSet<Transaction>();
        this.TransactionLeft = newTransaction;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
        TransactionCompletedByFollowee = new int[followees.length];
    }

    public void setPendingTransaction(Set<Transaction> TransactionLeft) {
        if (TransactionLeft.isEmpty()) {
            return;
        }
        else{
            this.TransactionLeft.clear();
            this.TransactionLeft.addAll(TransactionLeft);
            Transaction[] transact = TransactionLeft.toArray(new Transaction[0]);
            if (transact.length > 0) {
                myTransaction = transact[0];
            }
        }
    }

    public Set<Transaction> sendToFollowers() {
        Set<Transaction> remainingTransaction = new HashSet<Transaction>();
        if (round > TotalRound) {
            remainingTransaction.addAll(TransactionLeft);
            TransactionLeft.clear();
        } else {
            if (myTransaction != null) {
                remainingTransaction.add(myTransaction);
            }
        }

        return remainingTransaction;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        round++;

        Map<Integer, Set<Transaction>> data = new HashMap<>();
        for (Candidate eachcandidate : candidates) {
            if (followees[eachcandidate.sender]) {
                if (!data.containsKey(eachcandidate.sender)) {
                    data.put(eachcandidate.sender, new HashSet<>());
                }

                data.get(eachcandidate.sender).add(eachcandidate.tx);
            }
        }

        if (round <= TotalRound) {
            for (int i = 0; i < followees.length; i++) {
                if (followees[i]) {
                    if (data.containsKey(i)) {
                        Set<Transaction> transact = data.get(i);
                        if (transact.size() == 1) {
                            TransactionCompletedByFollowee[i]++;
                        }
                    }
                }
            }
        }
        if (round > TotalRound) {
            for (int i = 0; i < followees.length; i++) {
                if (followees[i] && TransactionCompletedByFollowee[i] == TotalRound) {
                    if (data.containsKey(i)) {
                        TransactionLeft.addAll(data.get(i));
                    }
                }
            }
        }
    }
}
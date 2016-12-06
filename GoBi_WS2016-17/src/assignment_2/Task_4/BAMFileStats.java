package assignment_2.Task_4;

public class BAMFileStats {

	private String name;
	// gesamt counts
	private int ok = 0, partial = 0, wrongChr = 0, everythingElse = 0;
	// gesamt counts aufgeteilt in fw und rw
	private int okFW = 0, okRW = 0, partialFW = 0, partialRW = 0, wrongChrFW = 0, wrongChrRW = 0, everythingElseFW = 0,
			everythingElseRW = 0;

	// gesamt counts task2
	public int fwSpliced = 0, rwSpliced = 0, spliced = 0, mismatchesFW = 0, mismatchesRW = 0, mismatches = 0,
			splitLonger5NoMisFW = 0, splitLonger5NoMisRW = 0, splitLonger5NoMis = 0;
	// ok
	public int okfwSpliced = 0, okrwSpliced = 0, okspliced = 0, okmismatchesFW = 0, okmismatchesRW = 0,
			okmismatches = 0, oksplitLonger5NoMisFW = 0, oksplitLonger5NoMisRW = 0, oksplitLonger5NoMis = 0;
	// partial
	public int partialfwSpliced = 0, partialrwSpliced = 0, partialspliced = 0, partialmismatchesFW = 0,
			partialmismatchesRW = 0, partialmismatches = 0, partialsplitLonger5NoMisFW = 0,
			partialsplitLonger5NoMisRW = 0, partialsplitLonger5NoMis = 0;
	// wrongChr
	public int wrongChrfwSpliced = 0, wrongChrrwSpliced = 0, wrongChrspliced = 0, wrongChrmismatchesFW = 0,
			wrongChrmismatchesRW = 0, wrongChrmismatches = 0, wrongChrsplitLonger5NoMisFW = 0,
			wrongChrsplitLonger5NoMisRW = 0, wrongChrsplitLonger5NoMis = 0;
	// everythingElse
	public int everythingElsefwSpliced = 0, everythingElserwSpliced = 0, everythingElsespliced = 0,
			everythingElsemismatchesFW = 0, everythingElsemismatchesRW = 0, everythingElsemismatches = 0,
			everythingElsesplitLonger5NoMisFW = 0, everythingElsesplitLonger5NoMisRW = 0,
			everythingElsesplitLonger5NoMis = 0;

	public String getStatsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "\t");
		sb.append(ok + "\t" + partial + "\t" + wrongChr + "\t" + everythingElse + "\t");
		sb.append(okFW + "\t" + okRW + "\t" + partialFW + "\t" + partialRW + "\t" + wrongChrFW + "\t" + wrongChrRW
				+ "\t" + everythingElseFW + "\t" + everythingElseRW + "\t");
		sb.append(fwSpliced + "\t" + rwSpliced + "\t" + spliced + "\t" + mismatchesFW + "\t" + mismatchesRW + "\t"
				+ mismatches + "\t" + splitLonger5NoMisFW + "\t" + splitLonger5NoMisRW + "\t" + splitLonger5NoMis
				+ "\t");
		sb.append(okfwSpliced + "\t" + okrwSpliced + "\t" + okspliced + "\t" + okmismatchesFW + "\t" + okmismatchesRW
				+ "\t" + okmismatches + "\t" + oksplitLonger5NoMisFW + "\t" + oksplitLonger5NoMisRW + "\t"
				+ oksplitLonger5NoMis + "\t");
		sb.append(partialfwSpliced + "\t" + partialrwSpliced + "\t" + partialspliced + "\t" + partialmismatchesFW + "\t"
				+ partialmismatchesRW + "\t" + partialmismatches + "\t" + partialsplitLonger5NoMisFW + "\t"
				+ partialsplitLonger5NoMisRW + "\t" + partialsplitLonger5NoMis + "\t");
		sb.append(everythingElsefwSpliced + "\t" + everythingElserwSpliced + "\t" + everythingElsespliced + "\t"
				+ everythingElsemismatchesFW + "\t" + everythingElsemismatchesRW + "\t" + everythingElsemismatches
				+ "\t" + everythingElsesplitLonger5NoMisFW + "\t" + everythingElsesplitLonger5NoMisRW + "\t"
				+ everythingElsesplitLonger5NoMis + "\t");
		sb.append(wrongChrfwSpliced + "\t" + wrongChrrwSpliced + "\t" + wrongChrspliced + "\t" + wrongChrmismatchesFW
				+ "\t" + wrongChrmismatchesRW + "\t" + wrongChrmismatches + "\t" + wrongChrsplitLonger5NoMisFW + "\t"
				+ wrongChrsplitLonger5NoMisRW + "\t" + wrongChrsplitLonger5NoMis);
		return sb.toString();
	}

	public static String getHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"name\tok\tpartial\twrongChr\teverythingElse\tokFW\tokRW\tpartialFW\tpartialRW\twrongChrFW\twrongChrRW\teverythingElseFW\teverythingElseRW\tfwSpliced\trwSpliced\tspliced\tmismatchesFW\tmismatchesRW\tmismatches\tsplitLonger5NoMisFW\tsplitLonger5NoMisRW\tsplitLonger5NoMis\tokfwSpliced\tokrwSpliced\tokspliced\tokmismatchesFW\tokmismatchesRW\tokmismatches\toksplitLonger5NoMisFW\toksplitLonger5NoMisRW\toksplitLonger5NoMis\tpartialfwSpliced\tpartialrwSpliced\tpartialspliced\tpartialmismatchesFW\tpartialmismatchesRW\tpartialmismatches\tpartialsplitLonger5NoMisFW\tpartialsplitLonger5NoMisRW\tpartialsplitLonger5NoMis\teverythingElsefwSpliced\teverythingElserwSpliced\teverythingElsespliced\teverythingElsemismatchesFW\teverythingElsemismatchesRW\teverythingElsemismatches\teverythingElsesplitLonger5NoMisFW\teverythingElsesplitLonger5NoMisRW\teverythingElsesplitLonger5NoMis\twrongChrfwSpliced\twrongChrrwSpliced\twrongChrspliced\twrongChrmismatchesFW\twrongChrmismatchesRW\twrongChrmismatches\twrongChrsplitLonger5NoMisFW\twrongChrsplitLonger5NoMisRW\twrongChrsplitLonger5NoMis");
		return sb.toString();
	}

	public BAMFileStats(String name) {
		this.name = name;
	}

	public void addStatsOf(ReadPair rp) {
		if (rp.spliced)
			spliced++;
		if (rp.mismatches)
			mismatches++;
		if (rp.splitLonger5NoMis)
			splitLonger5NoMis++;
		switch (rp.getOKStat()) {
		case "ok":
			ok++;
			if (rp.spliced)
				okspliced++;
			if (rp.mismatches)
				okmismatches++;
			if (rp.splitLonger5NoMis)
				oksplitLonger5NoMis++;
			break;
		case "partial":
			partial++;
			if (rp.spliced)
				partialspliced++;
			if (rp.mismatches)
				partialmismatches++;
			if (rp.splitLonger5NoMis)
				partialsplitLonger5NoMis++;
			break;
		case "everythingElse":
			everythingElse++;
			if (rp.spliced)
				everythingElsespliced++;
			if (rp.mismatches)
				everythingElsemismatches++;
			if (rp.splitLonger5NoMis)
				everythingElsesplitLonger5NoMis++;
			break;
		case "wrongChr":
			wrongChr++;
			if (rp.spliced)
				wrongChrspliced++;
			if (rp.mismatches)
				wrongChrmismatches++;
			if (rp.splitLonger5NoMis)
				wrongChrsplitLonger5NoMis++;
			break;
		}

		if (rp.fwSpliced)
			fwSpliced++;
		if (rp.mismatchesFW)
			mismatchesFW++;
		if (rp.splitLonger5NoMisFW)
			splitLonger5NoMisFW++;
		switch (rp.fwStat) {
		case "ok":
			okFW++;
			if (rp.fwSpliced)
				okfwSpliced++;
			if (rp.mismatchesFW)
				okmismatchesFW++;
			if (rp.splitLonger5NoMisFW)
				oksplitLonger5NoMisFW++;
			break;
		case "partial":
			partialFW++;
			if (rp.fwSpliced)
				partialfwSpliced++;
			if (rp.mismatchesFW)
				partialmismatchesFW++;
			if (rp.splitLonger5NoMisFW)
				partialsplitLonger5NoMisFW++;
			break;
		case "everythingElse":
			everythingElseFW++;
			if (rp.fwSpliced)
				everythingElsefwSpliced++;
			if (rp.mismatchesFW)
				everythingElsemismatchesFW++;
			if (rp.splitLonger5NoMisFW)
				everythingElsesplitLonger5NoMisFW++;
			break;
		case "wrongChr":
			wrongChrFW++;
			if (rp.fwSpliced)
				wrongChrfwSpliced++;
			if (rp.mismatchesFW)
				wrongChrmismatchesFW++;
			if (rp.splitLonger5NoMisFW)
				wrongChrsplitLonger5NoMisFW++;
			break;
		}

		if (rp.rwSpliced)
			rwSpliced++;
		if (rp.mismatchesRW)
			mismatchesRW++;
		if (rp.splitLonger5NoMisRW)
			splitLonger5NoMisRW++;
		switch (rp.rwStat) {
		case "ok":
			okRW++;
			if (rp.rwSpliced)
				okrwSpliced++;
			if (rp.mismatchesRW)
				okmismatchesRW++;
			if (rp.splitLonger5NoMisRW)
				oksplitLonger5NoMisRW++;
			break;
		case "partial":
			partialRW++;
			if (rp.rwSpliced)
				partialrwSpliced++;
			if (rp.mismatchesRW)
				partialmismatchesRW++;
			if (rp.splitLonger5NoMisRW)
				partialsplitLonger5NoMisRW++;
			break;
		case "everythingElse":
			everythingElseRW++;
			if (rp.rwSpliced)
				everythingElserwSpliced++;
			if (rp.mismatchesRW)
				everythingElsemismatchesRW++;
			if (rp.splitLonger5NoMisRW)
				everythingElsesplitLonger5NoMisRW++;
			break;
		case "wrongChr":
			wrongChrRW++;
			if (rp.rwSpliced)
				wrongChrrwSpliced++;
			if (rp.mismatchesRW)
				wrongChrmismatchesRW++;
			if (rp.splitLonger5NoMisRW)
				wrongChrsplitLonger5NoMisRW++;
			break;
		}
	}

	public int[] getOverAllCounts() {
		return new int[] { ok, partial, wrongChr, everythingElse };
	}

	public int[] getOverallFWCounts() {
		return new int[] { okFW, partialFW, wrongChrFW, everythingElseFW };
	}

	public int[] getOverallRWCounts() {
		return new int[] { okRW, partialRW, wrongChrRW, everythingElseRW };
	}

	public String getName() {
		return name;
	}

	public int getOk() {
		return ok;
	}

	public int getPartial() {
		return partial;
	}

	public int getWrongChr() {
		return wrongChr;
	}

	public int getEverthingElse() {
		return everythingElse;
	}

}

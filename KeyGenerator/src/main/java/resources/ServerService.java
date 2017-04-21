package resources;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServerService {

	Scheduler scheduler;
	JobDetail job;
	static TreeMap<KeyDetails, Long> unAssignedMap = new TreeMap<KeyDetails, Long>();
	static Map<String, Long> unAssignedMap2 = new HashMap<String, Long>();
	static Map<String, Long> assignedMap = new HashMap<String, Long>();
	private long keep_alive_time = 300000;
	private long unBlockTime = 60000;
	Random rand = new Random();

	public ServerService() throws SchedulerException {
		super();
		scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
	}

	public String generate() {
		synchronized (ServerService.class) {

			KeyDetails keyDetails = new KeyDetails();
			keyDetails.settimeStamp(System.currentTimeMillis());
			keyDetails.setKey(String.valueOf(rand.nextInt(10000)) + System.currentTimeMillis());
			unAssignedMap.put(keyDetails, keyDetails.gettimeStamp());
			unAssignedMap2.put(keyDetails.getKey(), keyDetails.gettimeStamp());
			return "Successfully generated the key : " + keyDetails.getKey();

		}
	}

	public String getApiKey() {
		synchronized (ServerService.class) {

			if (!unAssignedMap.isEmpty()) {
				Entry<KeyDetails, Long> firstEntry = unAssignedMap.firstEntry();
				if (System.currentTimeMillis() - firstEntry.getValue() < keep_alive_time) {

					JobKey jobKeyA = new JobKey("unBolckJob" + firstEntry.getKey().getKey(),
							"group" + firstEntry.getKey().getKey());
					job = JobBuilder.newJob(UnblockJob.class).withIdentity(jobKeyA).build();

					Trigger trigger = TriggerBuilder.newTrigger()
							.withIdentity(
									"unBlockTrigger-" + firstEntry.getKey().getKey() + "-"
											+ firstEntry.getKey().gettimeStamp(),
									"group" + firstEntry.getKey().getKey())
							.startAt(new Date(System.currentTimeMillis() + unBlockTime))
							.withSchedule(
									SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
							.build();

					try {
						scheduler.scheduleJob(job, trigger);
					} catch (SchedulerException e) {

					}
					assignedMap.put(firstEntry.getKey().getKey(), firstEntry.getKey().gettimeStamp());
					unAssignedMap.remove(firstEntry.getKey());
					unAssignedMap2.remove(firstEntry.getKey().getKey());

					return "Successfully assigned the key : " + firstEntry.getKey().getKey();
				}
				unAssignedMap.clear();
				unAssignedMap2.clear();

			}

			return null;
		}

	}

	public String unBlockKey(String x) {
		synchronized (ServerService.class) {

			if (assignedMap.get(x) != null) {
				if (System.currentTimeMillis() - assignedMap.get(x) < keep_alive_time) {
					KeyDetails keyDetails = new KeyDetails();
					keyDetails.setKey(x);
					keyDetails.settimeStamp(assignedMap.get(x));
					unAssignedMap.put(keyDetails, keyDetails.gettimeStamp());
					unAssignedMap2.put(keyDetails.getKey(), keyDetails.gettimeStamp());
				}
				assignedMap.remove(x);
				return "Successfully unblocked the key : " + x;
			} else
				return null;
		}
	}

	public String deleteKey(String x) {
		synchronized (ServerService.class) {
			if (assignedMap.containsKey(x)) {
				if (System.currentTimeMillis() - assignedMap.get(x) < keep_alive_time) {
					assignedMap.remove(x);
					return "Successfully deleted the key : " + x;
				}
				assignedMap.remove(x);
			} else {
				if (unAssignedMap2.containsKey(x)) {
					KeyDetails keyDetails = new KeyDetails();
					keyDetails.setKey(x);
					keyDetails.settimeStamp(unAssignedMap2.get(x));
					if (System.currentTimeMillis() - unAssignedMap2.get(x) < keep_alive_time) {
						unAssignedMap.remove(keyDetails);
						unAssignedMap2.remove(keyDetails.getKey());
						return "Successfully deleted the key : " + x;
					}
					unAssignedMap.remove(keyDetails);
					unAssignedMap2.remove(keyDetails.getKey());
				}
			}
			return null;
		}
	}

	public String keepAlive(String x) {
		synchronized (ServerService.class) {

			if (assignedMap.get(x) != null) {
				if (System.currentTimeMillis() - assignedMap.get(x) < keep_alive_time) {
					assignedMap.put(x, System.currentTimeMillis());
					return "Successfully keptalive the key : " + x;
				}

			} else {
				if (unAssignedMap2.get(x) != null) {
					if (System.currentTimeMillis() - unAssignedMap2.get(x) < keep_alive_time) {
						KeyDetails keyDetails = new KeyDetails();
						keyDetails.setKey(x);
						keyDetails.settimeStamp(System.currentTimeMillis());
						unAssignedMap.put(keyDetails, keyDetails.gettimeStamp());
						unAssignedMap2.put(keyDetails.getKey(), keyDetails.gettimeStamp());
						return "Successfully keptalive the key : " + x;
					}
				}
			}
			return null;

		}
	}

}

package resources;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class UnblockJob implements Job {

	private long keep_alive_time = 300000;

	public void execute(JobExecutionContext context) throws JobExecutionException {

		synchronized (ServerService.class) {
			String triggerName = context.getTrigger().getKey().getName();
			String[] stringArray = triggerName.split("-");
			if (ServerService.assignedMap.get(stringArray[1]) != null) {
				if (System.currentTimeMillis() - Long.valueOf(stringArray[2]) < keep_alive_time) {
					KeyDetails keyDetails = new KeyDetails();
					keyDetails.setKey(stringArray[1]);
					keyDetails.settimeStamp(ServerService.assignedMap.get(stringArray[1]));
					ServerService.unAssignedMap.put(keyDetails, keyDetails.gettimeStamp());
					ServerService.unAssignedMap2.put(keyDetails.getKey(), keyDetails.gettimeStamp());
				} else {
					ServerService.assignedMap.remove(stringArray[1]);
				}
			}
		}

	}

}

package resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server/key")
public class Controller {

	@Autowired
	ServerService serverService;

	@RequestMapping(method = RequestMethod.GET, value = "/assign")
	public ResponseEntity<String> getKey() {

		String response = serverService.getApiKey();
		if (response == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No key found to be assigned!!");
		}
		return ResponseEntity.ok(response);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/generate")
	public ResponseEntity<String> generateKey() {
		String response = serverService.generate();
		return ResponseEntity.ok(response);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/unblock/{key}")
	public ResponseEntity<String> unBlockKey(@PathVariable String key) {
		String response = serverService.unBlockKey(key);
		if (response != null)
			return ResponseEntity.ok(response);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No key found to be unblocked!!");

	}

	@RequestMapping(method = RequestMethod.GET, value = "/delete/{key}")
	public ResponseEntity<String> deleteKey(@PathVariable String key) {
		String response = serverService.deleteKey(key);
		if (response != null)
			return ResponseEntity.ok(response);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No key found with key-value : " + key);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/keepalive/{key}")
	public ResponseEntity<String> keepLive(@PathVariable String key) {
		String response = serverService.keepAlive(key);
		if (response != null)
			return ResponseEntity.ok(response);
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body("No key found to be kept alive with key value : " + key);

	}

}

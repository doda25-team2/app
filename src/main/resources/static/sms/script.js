$(document).ready(function() {

	function getSMS() {
		return $("textarea").val().trim();
	}
	
	function getGuess() {
		return $("input[name='guess']:checked").val().trim();
	}
	
	function resetBackground() {
		$("body").removeClass("bg-correct bg-incorrect bg-error");
	}
	
	function cleanResult() {
		$("#result").removeClass("correct incorrect error");
		$("#result").html("");
		resetBackground();
	}

	$("button").click(function(e) {
		e.stopPropagation();
		e.preventDefault();

		var sms = getSMS();
		var guess = getGuess();
		
		if (!sms) {
			showError("Please enter an SMS message.");
			return;
		}
		
		// Show loading state
		$(this).prop("disabled", true).text("Checking...");
		
		$.ajax({
			type: "POST",
			url: "./",
			data: JSON.stringify({"sms": sms, "guess": guess}),
			contentType: "application/json",
			dataType: "json",
			success: handleResult,
			error: handleError,
			complete: function() {
				$("button").prop("disabled", false).text("Check");
			}
		});
	});

	function handleResult(res) {
		var wasRight = res.result === getGuess();
		var classifierResult = res.result === "spam" ? "SPAM" : "HAM";

		cleanResult();
		
		// Add background color based on result
		$("body").addClass(wasRight ? "bg-correct" : "bg-incorrect");
		
		// Update result message with more detail
		$("#result").addClass(wasRight ? "correct" : "incorrect");
		
		var icon = wasRight ? "✓" : "✗";
		var message = wasRight 
			? icon + " The classifier agrees! It also thinks this is " + classifierResult + "."
			: icon + " The classifier disagrees. It thinks this is " + classifierResult + ".";
		
		$("#result").html(message).fadeIn(300);
	}
	
	function showError(message) {
		cleanResult();
		$("body").addClass("bg-error");
		$("#result").addClass("error");
		$("#result").html(message).fadeIn(300);
	}
	
	function handleError(e) {
		showError("An error occurred. Please check the server log.");
	}
	
	// Reset state when user starts typing or changes selection
	$("textarea").on("input", function() {
		$("#result").fadeOut(200);
		resetBackground();
	});
	
	$("input[name='guess']").on("change", function() {
		$("#result").fadeOut(200);
		resetBackground();
	});
});
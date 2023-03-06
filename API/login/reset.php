<?php
    require_once './db_functions.php';
	$db = new User_Functions();
	$row=$db->getUserByToken($_GET['key']);
	$token=$_GET['key'];
	//if no token from db then kill the page
	if(empty($row['token'])){
		$stop = 'Invalid token provided, please use the link provided in the reset email.';
	} elseif($row['state'] == 'False') {
		$stop = 'Your password has already been changed!';
	}
	if(isset($stop)){
		echo "<p class='bg-danger'>$stop</p>";
	}
	else{?>
		<form role="form" method="post" action="" autocomplete="off">
		<div class="row">
			<div class="col-xs-6 col-sm-6 col-md-6">
				<div class="form-group">
					<input type="password" name="password" id="password" class="form-control input-lg" placeholder="Password" tabindex="1">
				</div>
			</div>
			<div class="col-xs-6 col-sm-6 col-md-6">
				<div class="form-group">
					<input type="password" name="passwordConfirm" id="passwordConfirm" class="form-control input-lg" placeholder="Confirm Password" tabindex="1">
				</div>
			</div>
		</div>
		
		<hr>
		<div class="row">
			<div class="col-xs-6 col-md-6"><input type="submit" name="submit" value="Change Password" class="btn btn-primary btn-block btn-lg" tabindex="3"></div>
		</div>
		</form>
	<?php }
	//if form has been submitted process it
	if(isset($_POST['submit'])){

		//basic validation
		if(strlen($_POST['password']) < 3){
			$error = 'Password is too short.';
		}

		if(strlen($_POST['passwordConfirm']) < 3){
			$error = 'Confirm password is too short.';
		}

		if($_POST['password'] != $_POST['passwordConfirm']){
			$error = 'Passwords do not match.';
		}

		//if no errors have been created carry on
		if(!isset($error)){

			//hash the password
			$hashedpassword = sha1($password);
                        $db->updatePassword($hashedpassword,$token);
		        echo"Password has been reset";
                        exit;

			//else catch the exception and show the error.
			
			}else{
			echo $error;
		}

		}
		
	
?>
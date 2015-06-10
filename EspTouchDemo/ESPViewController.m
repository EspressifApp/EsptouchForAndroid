//
//  ESPViewController.m
//  EspTouchDemo
//
//  Created by 白 桦 on 3/23/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPViewController.h"
#import "ESPTouchTask.h"
#import "ESPTouchResult.h"
#import "ESP_NetUtil.h"

#import <SystemConfiguration/CaptiveNetwork.h>

// the three constants are used to hide soft-keyboard when user tap Enter or Return
#define HEIGHT_KEYBOARD 216
#define HEIGHT_TEXT_FIELD 30
#define HEIGHT_SPACE (6+HEIGHT_TEXT_FIELD)

@interface ESPViewController ()

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *_spinner;
@property (weak, nonatomic) IBOutlet UITextField *_pwdTextView;
@property (weak, nonatomic) IBOutlet UITextField *_taskResultCountTextView;
@property (weak, nonatomic) IBOutlet UIButton *_confirmCancelBtn;
@property (weak, nonatomic) IBOutlet UISwitch *_isSsidHiddenSwitch;

// to cancel ESPTouchTask when
@property (atomic, strong) ESPTouchTask *_esptouchTask;

// the state of the confirm/cancel button
@property (nonatomic, assign) BOOL _isConfirmState;

// without the condition, if the user tap confirm/cancel quickly enough,
// the bug will arise. the reason is follows:
// 0. task is starting created, but not finished
// 1. the task is cancel for the task hasn't been created, it do nothing
// 2. task is created
// 3. Oops, the task should be cancelled, but it is running
@property (nonatomic, strong) NSCondition *_condition;

@property (nonatomic, strong) UIButton *_doneButton;
@end

@implementation ESPViewController

- (IBAction)tapConfirmCancelBtn:(UIButton *)sender
{
    [self tapConfirmForResults];
}


- (void) tapConfirmForResults
{
    // do confirm
    if (self._isConfirmState)
    {
        [self._spinner startAnimating];
        [self enableCancelBtn];
        NSLog(@"ESPViewController do confirm action...");
        dispatch_queue_t  queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        dispatch_async(queue, ^{
            NSLog(@"ESPViewController do the execute work...");
            // execute the task
            NSArray *esptouchResultArray = [self executeForResults];
            // show the result to the user in UI Main Thread
            dispatch_async(dispatch_get_main_queue(), ^{
                [self._spinner stopAnimating];
                [self enableConfirmBtn];
                
                ESPTouchResult *firstResult = [esptouchResultArray objectAtIndex:0];
                // check whether the task is cancelled and no results received
                if (!firstResult.isCancelled)
                {
                    NSMutableString *mutableStr = [[NSMutableString alloc]init];
                    NSUInteger count = 0;
                    // max results to be displayed, if it is more than maxDisplayCount,
                    // just show the count of redundant ones
                    const int maxDisplayCount = 5;
                    if ([firstResult isSuc])
                    {
                        
                        for (int i = 0; i < [esptouchResultArray count]; ++i)
                        {
                            ESPTouchResult *resultInArray = [esptouchResultArray objectAtIndex:i];
                            [mutableStr appendString:[resultInArray description]];
                            [mutableStr appendString:@"\n"];
                            count++;
                            if (count >= maxDisplayCount)
                            {
                                break;
                            }
                        }
                        
                        if (count < [esptouchResultArray count])
                        {
                            [mutableStr appendString:[NSString stringWithFormat:@"\nthere's %lu more result(s) without showing\n",(unsigned long)([esptouchResultArray count] - count)]];
                        }
                        [[[UIAlertView alloc]initWithTitle:@"Execute Result" message:mutableStr delegate:nil cancelButtonTitle:@"I know" otherButtonTitles:nil]show];
                    }
                    
                    else
                    {
                        [[[UIAlertView alloc]initWithTitle:@"Execute Result" message:@"Esptouch fail" delegate:nil cancelButtonTitle:@"I know" otherButtonTitles:nil]show];
                    }
                }
                
            });
        });
    }
    // do cancel
    else
    {
        [self._spinner stopAnimating];
        [self enableConfirmBtn];
        NSLog(@"ESPViewController do cancel action...");
        [self cancel];
    }
}

- (void) tapConfirmForResult
{
    // do confirm
    if (self._isConfirmState)
    {
        [self._spinner startAnimating];
        [self enableCancelBtn];
        NSLog(@"ESPViewController do confirm action...");
        dispatch_queue_t  queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        dispatch_async(queue, ^{
            NSLog(@"ESPViewController do the execute work...");
            // execute the task
            ESPTouchResult *esptouchResult = [self executeForResult];
            // show the result to the user in UI Main Thread
            dispatch_async(dispatch_get_main_queue(), ^{
                [self._spinner stopAnimating];
                [self enableConfirmBtn];
                // when canceled by user, don't show the alert view again
                if (!esptouchResult.isCancelled)
                {
                    [[[UIAlertView alloc] initWithTitle:@"Execute Result" message:[esptouchResult description] delegate:nil cancelButtonTitle:@"I know" otherButtonTitles: nil] show];
                }
            });
        });
    }
    // do cancel
    else
    {
        [self._spinner stopAnimating];
        [self enableConfirmBtn];
        NSLog(@"ESPViewController do cancel action...");
        [self cancel];
    }
}

#pragma mark - the example of how to cancel the executing task

- (void) cancel
{
    [self._condition lock];
    if (self._esptouchTask != nil)
    {
        [self._esptouchTask interrupt];
    }
    [self._condition unlock];
}

#pragma mark - the example of how to use executeForResults
- (NSArray *) executeForResults
{
    [self._condition lock];
    NSString *apSsid = self.ssidLabel.text;
    NSString *apPwd = self._pwdTextView.text;
    NSString *apBssid = self.bssid;
    BOOL isSsidHidden = [self._isSsidHiddenSwitch isOn];
    int taskCount = [self._taskResultCountTextView.text intValue];
    self._esptouchTask =
    [[ESPTouchTask alloc]initWithApSsid:apSsid andApBssid:apBssid andApPwd:apPwd andIsSsidHiden:isSsidHidden];
    [self._condition unlock];
    NSArray * esptouchResults = [self._esptouchTask executeForResults:taskCount];
    NSLog(@"ESPViewController executeForResult() result is: %@",esptouchResults);
    return esptouchResults;
}

#pragma mark - the example of how to use executeForResult

- (ESPTouchResult *) executeForResult
{
    [self._condition lock];
    NSString *apSsid = self.ssidLabel.text;
    NSString *apPwd = self._pwdTextView.text;
    NSString *apBssid = self.bssid;
    BOOL isSsidHidden = [self._isSsidHiddenSwitch isOn];
    self._esptouchTask =
    [[ESPTouchTask alloc]initWithApSsid:apSsid andApBssid:apBssid andApPwd:apPwd andIsSsidHiden:isSsidHidden];
    [self._condition unlock];
    ESPTouchResult * esptouchResult = [self._esptouchTask executeForResult];
    NSLog(@"ESPViewController executeForResult() result is: %@",esptouchResult);
    return esptouchResult;
}


// enable confirm button
- (void)enableConfirmBtn
{
    self._isConfirmState = YES;
    [self._confirmCancelBtn setTitle:@"Confirm" forState:UIControlStateNormal];
}

// enable cancel button
- (void)enableCancelBtn
{
    self._isConfirmState = NO;
    [self._confirmCancelBtn setTitle:@"Cancel" forState:UIControlStateNormal];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    self._isConfirmState = NO;
    self._pwdTextView.delegate = self;
    self._pwdTextView.keyboardType = UIKeyboardTypeASCIICapable;
    self._taskResultCountTextView.delegate = self;
    self._taskResultCountTextView.keyboardType = UIKeyboardTypeNumberPad;
    self._condition = [[NSCondition alloc]init];
    [self enableConfirmBtn];
}

#pragma mark - the follow codes are just to make soft-keyboard disappear at necessary time

// when out of pwd textview, resign the keyboard
- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    if (![self._pwdTextView isExclusiveTouch])
    {
        [self._pwdTextView resignFirstResponder];
    }
    if (![self._taskResultCountTextView isExclusiveTouch]) {
        [self._taskResultCountTextView resignFirstResponder];
    }
}

#pragma mark -  the follow three methods are used to make soft-keyboard disappear when user finishing editing

// when textField begin editing, soft-keyboard apeear, do the callback
-(void)textFieldDidBeginEditing:(UITextField *)textField
{
    CGRect frame = textField.frame;
    int offset = frame.origin.y - (self.view.frame.size.height - (HEIGHT_KEYBOARD+HEIGHT_SPACE));
    
    NSTimeInterval animationDuration = 0.30f;
    [UIView beginAnimations:@"ResizeForKeyboard" context:nil];
    [UIView setAnimationDuration:animationDuration];
    
    if(offset > 0)
    {
        self.view.frame = CGRectMake(0.0f, -offset, self.view.frame.size.width, self.view.frame.size.height);
    }
    
    [UIView commitAnimations];
}

// when user tap Enter or Return, disappear the keyboard
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

// when finish editing, make view restore origin state
-(void)textFieldDidEndEditing:(UITextField *)textField
{
    self.view.frame =CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
}

- (void) addButtonToKeyboard {
    // create custom button
    if (self._doneButton == nil) {
        self._doneButton  = [[UIButton alloc] initWithFrame:CGRectMake(0, 163, 106, 53)];
    }
    else {
        [self._doneButton setHidden:NO];
    }
    
    [self._doneButton addTarget:self action:@selector(doneButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    // locate keyboard view
    UIWindow* tempWindow = [[[UIApplication sharedApplication] windows] objectAtIndex:1];
    UIView* keyboard = nil;
    for(int i=0; i<[tempWindow.subviews count]; i++) {
        keyboard = [tempWindow.subviews objectAtIndex:i];
        // keyboard found, add the button
        if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 3.2) {
            if([[keyboard description] hasPrefix:@"<UIPeripheralHost"] == YES)
                [keyboard addSubview:self._doneButton];
        } else {
            if([[keyboard description] hasPrefix:@"<UIKeyboard"] == YES)
                [keyboard addSubview:self._doneButton];
        }
    }
}

- (void) doneButtonClicked:(id)Sender {
    //Write your code whatever you want to do on done button tap
    //Removing keyboard or something else
    if (![self._taskResultCountTextView isExclusiveTouch]) {
        [self._taskResultCountTextView resignFirstResponder];
    }
}

@end

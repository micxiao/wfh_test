from PIL import Image
import numpy as np
import os
from io import BytesIO
import pickle
import base64
import tensorflow as tf
from tensorflow.keras import layers, models


def load_image(bytes_data):
    bytes_data = base64.b64decode(bytes_data)
    image = BytesIO(bytes_data)
    img = Image.open(image)
    if img.mode != 'RGB':
        img = img.convert('RGB')
    img = img.resize((32, 32))
    np_img = np.array(img) / 255.0
    return np_img


def svm_classifier(np_img):
    np_img = np_img.reshape(-1)
    with open('svm_classifier_updated_gridsearch.pkl', 'rb') as f:
        loaded_classifier = pickle.load(f)
    result = int(loaded_classifier.predict([np_img])[0])
    proba = float(loaded_classifier.predict_proba([np_img])[0][result])
    print('svm', result, proba)
    return proba, result

def cnn_classifier(np_img):
    np_img = np.expand_dims(np_img, axis=0)
    loaded_classifier = tf.keras.models.load_model('my_model.keras')
    proba = float(loaded_classifier.predict(np_img)[0][0])
    result = int(proba > 0.5)
    print('cnn', proba, result)
    return proba, result



